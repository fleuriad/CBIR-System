
public class Test 
{
	public static void main(String[] args)
	{
		
		TraitementImage nv = new TraitementImage();
		nv.chargerImage("U:/lena.jpg");
		
		int ndg[] = null;
		int[] canny = null;
		int lbp[] = null;
		double harris[] = null;
		
		
		
		ndg = nv.NiveauxdeGris();
		//gradient = nv.Gradient();
		//canny = nv.canny(1.75);
		//lbp = nv.calculLBP();
		harris = nv.HarrisDetector(0.04);
		//nv.afficher(lbp);

		nv.enregisterImage("haaris.jpg", "jpg",harris);
		//nv.enregisterImage("canny.jpg", "jpg",canny);
		//nv.enregisterImage("gradient.jpg", "jpg",gradient);
		//System.out.println(canny[1]);
		/*
		int j=0;
		for(int i=0; i<nv.image.getHeight()*nv.image.getWidth(); i++)
		{
				System.out.print(harris[i]);
				System.out.print(' ');
				System.out.print(' ');
				if(j==nv.image.getHeight())
				{
					System.out.println();
					j=0;
				}
				j++;
	

		}*/
	}
	
}
