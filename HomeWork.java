public class HomeWork {
	public static void main(String[] args) {
		int[][] board = new int[64][64];
		/*int[][] b ={{0,1,0},
		{1,2,1},
		{1,0,0}};*/
	/*	int[][] board ={{2,2,0,1,0},
				{1,2,2,2,0},
				{1,0,0,0,2},
				{1,2,0,1,1},
				{0,0,2,2,0}};*/
		int numOfFruits = 5;
		while(true){
		Random rnd = new Random();
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length; j++){
				board[i][j] = rnd.nextInt(numOfFruits); 
			}
		}
		//Print(board);
		MaxminAgent p = new MaxminAgent(board.length, board[0].length, numOfFruits);
		
		//int[] m =p.nextMove(b, 200000l);
		int[] m1 ={0,0};
		int[][] b1 = p.getCopyOfBoard(board);
		p.minmaxPick(b1, m1);
		}
	}