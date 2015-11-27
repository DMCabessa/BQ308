package ultrametricTree;


public class QuickSort
{
	public static void ordenar(Edge[] vetor)
	{
		ordenar(vetor, 0, vetor.length - 1);
	}

	private static void ordenar(Edge[] vetor, int inicio, int fim)
	{
		if (inicio < fim)
		{
			int posicaoPivo = separar(vetor, inicio, fim);
			ordenar(vetor, inicio, posicaoPivo - 1);
			ordenar(vetor, posicaoPivo + 1, fim);
		}
	}

	private static int separar(Edge[] vetor, int inicio, int fim)
	{
		Edge pivo = vetor[inicio];
		int i = inicio + 1, f = fim;
		while (i <= f)
		{
			if (vetor[i].getWeight() <= pivo.getWeight())
				i++;
			else if (pivo.getWeight() < vetor[f].getWeight())
				f--;
			else
			{
				Edge troca = vetor[i];
				vetor[i] = vetor[f];
				vetor[f] = troca;
				i++;
				f--;
			}
		}
		vetor[inicio] = vetor[f];
		vetor[f] = pivo;
		return f;
	}
}
