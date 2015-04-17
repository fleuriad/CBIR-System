
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

public class TraitementImage 
{
	//Variables membres
	public BufferedImage image;
	private WritableRaster raster;
	private int[] tab_lbp;//tableau contenant les lbp de l'image
	private int[] imageNDG;//tableau de pixels pour l'image en niveau de gris
	private int[] imageGradient;//tableau de pixels pour le gradient de l'image
	public int[] canny;
	private int[] hog;
	private double []harris;//matrice contenant le haaris de l'image
	private int[] H;//gradient horizontal
	private int[] V;//gradient vertical
	double[] tab_angle;//matrice contenant l'orientation de chaque pixel
	
	//Fonctions membres
	public TraitementImage()
	{
		
		//Constructeur 
	}

/*En java, afin de transformer une image RGB en une image en blanc et noir, on dois
 * juste additionner les 3 composantes RGB de chaque pixel et diviser cette somme par 3
 */
 
public int[] NiveauxdeGris()
	{
		imageNDG = new int[image.getWidth()*image.getHeight()];
		for(int a=0; a < image.getWidth()*image.getHeight(); a++)
		{
			imageNDG[a]=0; // On initialise notre tableau
		}
		int [] composantes = new int [3];
		for(int i = 0; i<image.getHeight(); i++)//On va parcourir les colonnes
		{
			for(int j = 0; j<image.getWidth(); j++)//On va parcourir les lignes
			{
				composantes = this.getPixel(j, i);//Cette fonction renvoie un tableau de 3 cases contenant la composante R en indice 0, la composante G en indice 1 et la composante B en indice 2 pour chaque pixel 
				imageNDG[i*image.getWidth()+j] = (int) ((composantes[0] + composantes[1] + composantes[2])/3);// On sauvegarde la nouvelle valeur valeur de notre pixel dans le tableau imageNDG
			}
		}
		return imageNDG;//On retourne l'image en noir et blanc
	}
	
/*La fonction ci-dessous nous permet de calculer le gradient d'une image, on va deplacer un masque de convolution 
 * qui est ici une matrice de taille 3x3 autour de chacun des pixels et calculer le gradient horizontal
 * et le gradient vertical ensuite nous allons additionner pour chaque pixel, son gradient horizontal
 * et son gradient vertical, si la somme est supérieur à 255, cette somme est automatiquement remis à
 * 255
 */
public int[] Gradient()
	{
		/*On definit avec nos entiers ci dessous notre masque de convolution*/
		int i00, i01, i02;
		int i10,	  i12;
		int i20, i21, i22;
		int i=0;
		imageGradient = new int[image.getWidth()*image.getHeight()];//On crée une matrice de la taille de l'image de départ
		H = new int[image.getWidth()*image.getHeight()];//Notre matrice de gradient horizontale
		V = new int[image.getWidth()*image.getHeight()];//Notre matrice de gradient verticale
		int GN;
		
		for(i=0; i<image.getWidth()*(image.getHeight()-2)-2; i++)
		{
			i00 = imageNDG[i]; 						i01 = imageNDG[i+1];  							i02 = imageNDG[i+2];
			i10 = imageNDG[i+image.getWidth()];	  													i12 = imageNDG[i+image.getWidth()+2];
			i20 = imageNDG[i+2*image.getWidth()];	i21 = imageNDG[i+2*image.getWidth()+1]; 		i22 = imageNDG[i+2*image.getWidth()+2];
			
			 H[i] = - i00 - 2*i01 - i02 + i20 + 2*i21 + i22;//On calcule le gradient horizontal
			 V[i] = - i00 + i02 - 2*i10 + 2*i12 - i20 + i22;//On calcule le gradient vertical
			 
			 GN = Math.abs(H[i])+ Math.abs(V[i]);//On fais la somme des deux gradients  sans oublier les valeurs absolues
			 if(GN>255)//Si le gradient total est superieur à 255
			 {
				 GN = 255;//Le gradient totale est égale à 255
			 }
			 imageGradient[i+1] = GN;//On enregistre le resultat dans le tableau imageGradient
			 
		}
		
		return imageGradient;//On renvoie la valeur de imageGradient
	}
	
/*Cette fonction nous permet de retourner la valeur d'un pixel (tableau de coordonées RGB) selon les coordonnées  de ce pixel*/
public int[] getPixel(int x, int y)
	{
		int[] val_pixel = new int[3]; //On crée un tableau de taille 3
		raster.getPixel(x, y, val_pixel); // Grace à cette fonction, les valeurs des composantes du pixel sont stockés dans le tableau val_pixel
		return val_pixel; //On renvoie notre tableau
	}

/*Cette fonction nous permet de modifier la couleur d'un pixel*/
public void setPixel(int x, int y, int[] color)
	{
		raster.setPixel(x, y, color);
	}
	
/*Cette fonction ci-dessous va nous permettre de charger une image en memoire
 * Ensuite avec des fonctions propres a java, nous allons pouvoir lire le 
 * tableau, pour info, nous devons créer un raster pour pouvoir accéder au 
 * tableau de pixels de l'image
 */
public void chargerImage(String nomImage)
	{
		image = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB); //On creé un image de type BufferedImage
		try 
		{
			image = ImageIO.read(new File(nomImage));//Ici, on charge en mémoire l'image passé en paramètre
			raster = image.getRaster();
	    }catch (IOException e) 
	   {}
		
	}
	
/*Fonction de base pour enregisrer un objet de type BufferedImage
 * dans une image dans le format souhaité, a noter que le format
 * d'entrée doit etre le meme pour le format de sortie
 */
public void enregisterImage(String nomImage, String extension)
	{
		try {
		    // retrieve image
			File outputfile = new File(nomImage);
		    ImageIO.write(image, extension, outputfile);//L'enregistrement du fichier sur le disque s'effectue ici
			} 
			catch (IOException e) 
		{
			
		}
	}

/*Fonction surchargé pour enregisrer un tableau de pixels (entiers)
 * dans une image avec le format souhaité, a noter que le format
 * d'entrée doit etre le meme pour le format de sortie
 */
public void enregisterImage(String nomImage, String extension, int[]tab)
	{
		try {
		    // retrieve image
			BufferedImage nv_image = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_BYTE_GRAY);// On crée une variable nv_image de type BufferedImage
			byte[] linearbuffer = new byte[image.getWidth()*image.getHeight()];
			for(int i=0; i<image.getWidth()*image.getHeight(); i++)
			{
				linearbuffer[i] = (byte)tab[i];//On va sauvegarder le tableau de pixels dans le buffer 
				nv_image.getRaster().setDataElements(0, 0,image.getWidth(),image.getHeight(),linearbuffer);
			}
			System.out.println("Enregistement" + " "+nomImage +" "+ "terminé");
			File outputfile = new File(nomImage);
		    ImageIO.write(nv_image, extension, outputfile);
			} 
			catch (IOException e) 
		{
			
		}
	}
	
	
public int[] canny(double alpha)
	{
		
		double[] bp = null;
		double[] bm = null;
		double gamma = 0.0;
		
		double a, b1, b2;
		bp = new double[image.getWidth()];
		bm = new double[image.getWidth()];
		
		int []ir = new int[image.getWidth()*image.getHeight()];
		for(int b=0; b < image.getWidth()*image.getHeight(); b++)
		{
			ir[b]=0;
		}
		//int i = 0;
		gamma = -((1.0-Math.exp(-alpha)))/(Math.exp(-alpha));
		a = - gamma * Math.exp(-alpha);
		b1 = -2.0 * Math.exp(-alpha);
		b2 = Math.exp(-alpha);
		
		
		for(int i = 0; i<image.getWidth(); i++)
		{
			bp[0] = 0.0;
			bp[1] = a *(double)imageNDG[i*image.getWidth()];
			for(int j=2; j<image.getWidth(); j++) bp[j] = a * (double)imageNDG[i*image.getWidth()+j-1] - b1*bp[j-1]- b2*bp[j-2];
				
			bm[image.getWidth()-1] = 0.0;
			bm[image.getWidth()-2] = a * (double)imageNDG[i*image.getWidth() + image.getWidth()-1] ;	
			for(int j1 = image.getWidth()-3; j1>=0; j1--) bm[j1] = - a*(double)imageNDG[i*image.getWidth()+j1+1] - b1*bm[j1+1]- b2*bm[j1+2];
	
			for(int l=0; l<image.getWidth(); l++) ir[i*image.getWidth()+l] = (int) (Math.abs(bp[l] + bm[l]) + 0.5);
		}
		for(int j=0;j<image.getHeight();j++)
		{
		   bp[0]=0.0;
		   bp[1]=a*(double)imageNDG[j];
		   for(int i=2;i<image.getHeight();i++) bp[i]=a*(double)imageNDG[(i-1)*image.getWidth()+j]-b1*bp[i-1]-b2*bp[i-2];

		   bm[image.getHeight()-1]= 0.0;
		   bm[image.getHeight()-2]= a*(double)imageNDG[(image.getWidth()-1)*image.getWidth()+j];
		   for(int i=image.getHeight()-3;i>=0;i--) bm[i]=-a*(double)imageNDG[(i+1)*image.getWidth()+j]-b1*bm[i+1]-b2*bm[i+2];

		   for(int i=0;i<image.getWidth();i++) ir[i*image.getWidth()+j] +=Math.abs(bp[i]+bm[i]);
		}
		
		double max = (double) ir[0];
		double min = (double) ir[0];
		canny = new int[image.getWidth()*image.getHeight()];
		for(int i2 = 0; i2<image.getHeight(); i2++)
		{
			for(int j = 0; j<image.getWidth(); j++)
			{
				if(ir[i2*image.getWidth()+j] > max)
				{
					max = ir[i2*image.getWidth()+j];
				}
				else if(ir[i2*image.getWidth()+j] < min)
				{
					min = ir[i2*image.getWidth()+j];
				}
			}
		}
		System.out.println(min);
		System.out.println(max);
		
		for(int l = 0; l <image.getHeight(); l++)
		{
			for(int k = 0; k<image.getWidth(); k++)
			{
				canny[l*image.getWidth()+k] = (int) ( (((double)ir[l*image.getWidth()+k] - min) /(max - min )) * 255.0);
			}
		}
		
		return canny;
	}

/*Cette fonction ci-dessous nous permet d'afficher une matrice de pixels d'entiers dans la fenetre 
 * d'execution
 */
public void afficher(int []t)
{
	int j=0;
	for(int i=0; i<image.getWidth()*image.getHeight();i++)
	{
		System.out.print(t[i]);
		System.out.print(' ');//Espace necessaire pour bien distinguer la valeur de nos pixels
		System.out.print(' ');//Espace necessaire pour bien distinguer la valeur de nos pixels
		if(j==image.getWidth())
		{
			System.out.println();//Une fois que le nombre de pixels à été atteint sur la ligne, on fait un retour à la ligne 
			j=0;
		}
		j++;
	}
}
/*Cette fonction ci-dessous va nous permettre de calculer le LBP de chacun des pixels de l'image*/
public int[] calculLBP()
{
	int[] seuil = new int[8];//tableau qui va contenir les seuils
	int[] contour = new int[8];
	tab_lbp = new int[image.getWidth()*image.getHeight()];
	
	int val = 0;
	double lbp = 0.0;
	int i = 0;
	/*On a créer juste en bas un masque qui va nous permettre de nous deplacer sur l'image*/
	for(i=0; i<image.getWidth()*(image.getHeight()-2)-2; i++)
	{
		
		contour[0] = imageNDG[i]; 							contour[1] = imageNDG[i+1];  							contour[2] = imageNDG[i+2];
		contour[7] = imageNDG[i+image.getWidth()];	  																contour[3] = imageNDG[i+image.getWidth()+2];
		contour[6] = imageNDG[i+2*image.getWidth()];		contour[5] = imageNDG[i+2*image.getWidth()+1]; 			contour[4] = imageNDG[i+2*image.getWidth()+2];
		
		val = imageNDG[i+image.getWidth()+1];//On va comparer cette valeur avec les valeurs des pixels autour
		
		for(int j = 0; j<7; j++)
		{
			if(val >= contour[j])//Si la valeur centrale est superieur à celle du pixel voisin
			{
				seuil[j] = 1;//notre seuil vaut 1
			}
			else
			{
				seuil[j] = 0;//sinon notre seul vaut 0
			}

		}
		
		lbp = this.conversion(seuil);//On va convertir le seuil en puissance de 2
		tab_lbp[i+1] = (int) lbp;//On sauvegarde le resultat dans notre tableau tab_lbp
		lbp = 0.0;//On n'oublie pas de mettre lbp à 0 pour le nouveau calcul sur le prochain pixel
	}
	
	return tab_lbp;
}

public int[] HOG()
{
	hog = new int[256];
	int i,j,v;
	
	for(v=0; v<256; v++)
	{
		hog[v] = 0;
	}
	for(i=0;i<image.getWidth(); i++)
	{
		for(j=0; j<image.getHeight(); j++)
		{
			v = canny[i*image.getWidth()+j];
			hog[v]++;
		}
	}
	return hog;
} 


public double[] HarrisDetector(double k)
{
	double i00, i01, i02;
	double i10,      i12;
	double i20, i21, i22;
	int i=0;
	
	double[] Gaussienne = new double[image.getWidth()*image.getHeight()];
	Gaussienne = this.gaussienne(imageNDG);
	
	double[] R = new double[image.getWidth()*image.getHeight()];
	harris = new double[image.getWidth()*image.getHeight()];

	double []H = new double[image.getWidth()*image.getHeight()];
	double []V = new double[image.getWidth()*image.getHeight()];

	double []HH =new double[image.getWidth()*image.getHeight()];
	double []VV = new double[image.getWidth()*image.getHeight()];

	double []trace = new double[image.getWidth()*image.getHeight()];
	double []det = new double[image.getWidth()*image.getHeight()];
	
	for(i=0; i<image.getWidth()*(image.getHeight()-2)-2; i++)
	{
		i00 = Gaussienne[i]; 						i01 = Gaussienne[i+1];  					i02 = Gaussienne[i+2];
		i10 = Gaussienne[i+image.getWidth()];	  												i12 = Gaussienne[i+image.getWidth()+2];
		i20 = Gaussienne[i+2*image.getWidth()];	 	i21 = Gaussienne[i+2*image.getWidth()+1]; 	i22 = Gaussienne[i+2*image.getWidth()+2];
		
		H[i] = - i00 - 2*i01 - i02 + i20 + 2*i21 + i22; // Gradient en x
		V[i] = - i00 + i02 - 2*i10 + 2*i12 - i20 + i22; //Gradient en y

		HH[i] = H[i]*H[i]; //Convoluer deux fois mon masque en x
		VV[i] = V[i]*V[i]; //Convoluer deux fois mon masque en Y

		trace[i] = HH[i] + VV[i];
		
		det[i] = HH[i]*VV[i] - H[i]*V[i]*H[i]*V[i];

		R[i] = det[i] - k*Math.pow(trace[i], 2);	
		harris[i] = R[i];
	}
	
	return harris;
	
}
/*Fonction conversion utilisée pour le calcul du LBP, c'est ici qu'on va effectuer la ponderation*/
public double conversion(int []tab)
{
	double val = 0.0;
	for(int i=0; i<tab.length; i++)
	{
		if(tab[i] == 1)
		{
			val = Math.pow(2, i) + val;
		}
		else
		{
			val = 1 + val;
		}
	}
	return val;
}
/*Fonction qui permet de faire une gaussienne sur l'image
 * Important pour le calcul des points caracteristiques de Haaris
 */
public double[] gaussienne(int []tab)
{
	double n_tab[] = new double[image.getHeight()*image.getWidth()];
	
	int i00, i01, i02;
	int i10,	  i12;
	int i20, i21, i22;
	int i=0;
	double Gau;
	
	for(i=0; i<image.getWidth()*(image.getHeight()-2)-2; i++)
	{
		i00 = imageNDG[i]; 						i01 = imageNDG[i+1];  							i02 = imageNDG[i+2];
		i10 = imageNDG[i+image.getWidth()];	  													i12 = imageNDG[i+image.getWidth()+2];
		i20 = imageNDG[i+2*image.getWidth()];	i21 = imageNDG[i+2*image.getWidth()+1]; 		i22 = imageNDG[i+2*image.getWidth()+2];
		
		 Gau = i00 + 2*i01 + i02 + 2*i10 + i20 + 2*i21 + i22 + 2*i12;
		 //V = - i00 + i02 - 2*i10 + 2*i12 - i20 + i22; 
		 /*
		 GN = Math.abs(H)+ Math.abs(V);
		 if(GN>255)
		 {
			 GN = 255;
		 }*/
		 n_tab[i+1] = Gau * 1/16;
		 
	}
	
	return n_tab;
}
/*Fonction surchargé pour enregisrer un tableau de pixels (double)
 * dans une image avec le format souhaité, a noter que le format
 * d'entrée doit etre le meme pour le format de sortie
 */
public void enregisterImage(String nomImage, String extension, double[]tab)
{
	try {
	    // retrieve image
		BufferedImage nv_image = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
		byte[] linearbuffer = new byte[image.getWidth()*image.getHeight()];
		for(int i=0; i<image.getWidth()*image.getHeight(); i++)
		{
			linearbuffer[i] = (byte)tab[i];
			nv_image.getRaster().setDataElements(0, 0,image.getWidth(),image.getHeight(),linearbuffer);
		}
		System.out.println("Enregistement" + " "+nomImage +" "+ "terminé");
		File outputfile = new File(nomImage);
	    ImageIO.write(nv_image, extension, outputfile);
		} 
		catch (IOException e) 
	{
		
	}
}
/*Cette fonction va nous permettre de calculer l'orientation de chaque pixel
 * Une fois qu'on a le gradient horizontal et le gradient vertical,
 * il suffit juste de calculer l'arctg de GY/GX et sauvegarder la valeur dans un tableau 
 */
public double[] angle()
{
	tab_angle = new double[image.getWidth()*image.getHeight()];
	
	for(int i=0; i<image.getWidth()*image.getHeight(); i++)
	{
		tab_angle[i] = Math.atan((double) (Math.abs(H[i])/Math.abs(V[i]))); 
	}
	
	return tab_angle;
}


/*Cette fonction ci-dessous nous permet d'afficher une matrice de pixels de double dans la fenetre 
 * d'execution
 */
public void afficher(double []t)
{
	int j=0;
	for(int i=0; i<image.getWidth()*image.getHeight();i++)
	{
		System.out.print(t[i]);
		System.out.print(' ');//Espace necessaire pour bien distinguer la valeur de nos pixels
		System.out.print(' ');//Espace necessaire pour bien distinguer la valeur de nos pixels
		if(j==image.getWidth())
		{
			System.out.println();//Une fois que le nombre de pixels à été atteint sur la ligne, on fait un retour à la ligne 
			j=0;
		}
		j++;
	}
}
/*
public void kmeans()
{
	int N[] = new int[4];
	//On va choisir 5 points au hasard
	int P1 = imageNDG[60*image.getWidth()+30]; 
	int P2 = imageNDG[50*image.getWidth()+40]; 
	int P3 = imageNDG[100*image.getWidth()+40]; 
	int P4 = imageNDG[40*image.getWidth()+70]; 
	int P5 = imageNDG[60*image.getWidth()+150]; 
	int X=0;
	for(int i=0; i<image.getWidth(); i++)
	{
		for(j=0; j<image.getHeight(); j++)
		{
			X = imageNDG[i*image.getWidth()+j];
			N[0]= P1 - X;
			N[1]= P2 - X;
			N[2]= P3 - X;
			N[3]= P4 - X;
			
			if(N[0]>N[1]| N[0]>N[1] | N[0]>N[2]| N[0]>N[3])
			{
				
			}
		}
		
	}
	
}
*/
}
