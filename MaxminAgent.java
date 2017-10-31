package Furitrage;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * @author magicyang
 * @date Oct 3, 2017 
 * This is an implementation of minmax search. According to the board size and remaining time,
 * Utilize different algorithm to save time.
 * Add minmax pruning to optimize the algorithm.
 *  
 */
public class MaxminAgent {
	int rows;
	int columns;
	int[][] x =new int[7][2];
	Random rnd;
	public MaxminAgent(int rows, int columns, int numOfFruits) {
		this.rows = rows;
		this.columns = columns;
		rnd = new Random();
		// TODO Auto-generated constructor stub
	} 
		
/*	public static long getCpuTime(){
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ?
				bean.getCurrentThreadCpuTime() : 0L;
	}
	
	public static long getUserTime(){
		ThreadMXBean bean = ManagementFactory.getThreadMXBean() ;
		return bean.isCurrentThreadCpuTimeSupported() ? 
				bean.getCurrentThreadUserTime() : 0L ;
	}
	
	public static long getSystemTime(){
		ThreadMXBean bean = ManagementFactory.getThreadMXBean() ;
		return bean.isCurrentThreadCpuTimeSupported() ? 
				(bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime()) : 0L ;
	}*/
	//@Override
	
	public int[] findMove(int[][] board, float time){
		// search the optimal move
		int[] m = new int[2];
		if(time*1000000 > 2500000l){
			//Remain more than 2 seconds, then choose Minmax search
			minmaxPick(board, m);
		}
		
		else if(time*1000000 > 500000l){
			//Remain more then 1 second, then choose Greedy Search;
			forwardCheck(board, m);
		}
		else if(time*1000000 > 0){
			greedyPick(board, m);
		}
		else{
			//Remain runs out, pick randomly;
			randomPick(board, m);
		}
		// m is the row and column number of chosen node 
		return m;
	}
	
	public static void Print(int[][] board){
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length; j++){
				if(board[i][j] != -1)
					System.out.printf("%d", board[i][j]);
				else
					System.out.printf("*");
			}
			System.out.println("");
		}
		System.out.println("");
	}
	
	public void minmaxPick(int[][] board, int[] m){
		int[][] copyBoard = getCopyOfBoard(board);
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		getScoreMap(board, scoreMap);
		//System.out.println(scoreMap);
		int depth = 1;
		// Adjust the forward checking depth according to board size
		if(scoreMap.size() <= 10){ 
			depth = 10;
		}
		else if(scoreMap.size() <= 20){
			depth = 5;
		}
		else if(scoreMap.size() <= 30){
			depth = 3;
		}
		
		int myScore = 0;
		int opScore = 0;
		int maxScore = Integer.MIN_VALUE;
		int maxNum = 0;
		int[][] new_board;
	//	int max = 0;
		for(int num : scoreMap.keySet()){
			new_board = getCopyOfBoard(copyBoard);
			myScore = scoreMap.get(num)*scoreMap.get(num);
			pick(new_board, num/columns, num%columns);
			fallDown(new_board);
		//	System.out.println("depth:"+depth+" "+num);
		//	Print(copyBoard);
			if(!isGameOver(new_board)){
				opScore = getScoreOfMin(new_board, depth-1, maxScore);
			}
			if(maxScore < myScore + opScore){
				maxScore = myScore + opScore;
				maxNum = num;
							
			}
		}
		//System.out.println(maxScore);
		//System.out.printf("choose:"+ m[0]+" "+m[1]);
		m[0] = maxNum/columns;
		m[1] = maxNum%columns; 
		//Print(board);
		//System.out.println(max);
	}
	
	public void minmaxPick1(int[][] board, int[] m){
		int[][] copyBoard = getCopyOfBoard(board);
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		getScoreMap(board, scoreMap);
		//System.out.println(scoreMap);
		int depth = 2;
		// Adjust the forward checking depth according to board size
		/*if(scoreMap.size() <= 7){ 
			depth = 7;
			System.out.println("e");
		}
		else if(scoreMap.size() <= 10){
			depth = 6;
			System.out.println("d");
		}
		else if(scoreMap.size() <= 16){
			depth = 5;
			System.out.println("c");
		}
		else if(scoreMap.size() <= 30){
			depth = 4;
			System.out.println("b");
		}
		else if(scoreMap.size() <= 100){
			depth = 3;
			System.out.println("a");
		}*/
		depth = (int)(Math.log(100000)/Math.log(scoreMap.size()));
		if(depth > 7)
			depth = 7;
		//System.out.println(depth);
		int myScore = 0;
		int opScore = 0;
		int maxScore = -4569760;
		int maxNum = 0;
		int[][] new_board;
	//	int max = 0;
		for(int num : scoreMap.keySet()){
			new_board = getCopyOfBoard(copyBoard);
			myScore = scoreMap.get(num)*scoreMap.get(num);
			opScore = 0;
		//	myScore = scoreMap.get(num);
			pick(new_board, num/columns, num%columns);
			fallDown(new_board);
		//	System.out.println("depth:"+depth+" "+num);
		//	Print(copyBoard);
			if(!isGameOver(new_board)){
				//opScore = getScoreOfMin1(new_board, depth-1, maxScore);
				opScore = getScoreOfMin1(new_board, depth-1, maxScore - myScore);
			}
			if(maxScore < myScore + opScore){
				maxScore = myScore + opScore;
				maxNum = num;			
			}
		}
		//System.out.println(maxScore);
		//System.out.printf("choose:"+ m[0]+" "+m[1]);
		m[0] = maxNum/columns;
		m[1] = maxNum%columns; 
		//Print(board);
		//System.out.println(max);
	}
	
	public int getScoreOfMin1(int[][] board, int depth, int a){
		int min = 4569760;
		//Utilize greedy as evaluate function 
		if(depth <= 0){
			int x = greedyPick(board, new int[2]);
			return Math.min(min, -1*x*x);
		}
		
		int[][] copyBoard = getCopyOfBoard(board);
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		getScoreMap(copyBoard, scoreMap);
		//System.out.println(scoreMap);
		int opScore = 0;
		int myScore = 0;
		for(int num : scoreMap.keySet()){
			opScore = scoreMap.get(num)*scoreMap.get(num);
		//	opScore = scoreMap.get(num);
			copyBoard = getCopyOfBoard(board);
			pick(copyBoard, num/columns, num%columns);
			fallDown(copyBoard);
		//	System.out.println("mindepth:"+depth+" "+num);
		//	Print(copyBoard);
			//System.out.println(isGameOver(copyBoard));
			myScore = 0;
			if(!isGameOver(copyBoard)){
				//myScore = getScoreOfMax1(copyBoard, depth-1, min);
				myScore = getScoreOfMax1(copyBoard, depth-1, min + opScore);
			}
	//		System.out.println(myScore+" " +opScore);
			if(myScore - opScore <= a)
				return myScore - opScore;
			min = Math.min(min, myScore - opScore);
		}
	//	System.out.println(min);
		return min;
	}
	
	public int getScoreOfMax1(int[][] board, int depth, int b){
		int max = -4569760;
		if(depth <= 0){
			int x = greedyPick(board, new int[2]);
			return Math.max(max, x*x);
		}
		int[][] copyBoard = getCopyOfBoard(board);
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		getScoreMap(copyBoard, scoreMap);
		//System.out.println(scoreMap);
		int myScore = 0;
		int opScore = 0;
		for(int num : scoreMap.keySet()){
			myScore = scoreMap.get(num)*scoreMap.get(num);
		//	myScore = scoreMap.get(num);
			copyBoard = getCopyOfBoard(board);
			pick(copyBoard, num/columns, num%columns);
			fallDown(copyBoard);
		//	System.out.println("maxdepth:"+depth+" "+num);
		//	Print(copyBoard);
			opScore = 0;
			if(!isGameOver(copyBoard)){
				//opScore = getScoreOfMin1(copyBoard, depth-1, max);
				opScore = getScoreOfMin1(copyBoard, depth-1, max - myScore);
			}
			if(myScore+opScore >= b)
				return myScore+opScore;
			max = Math.max(max, myScore+opScore);
		}
		return max;
	}
	
	
	//Get the score of min layer
		public int getScoreOfMin(int[][] board, int depth, int a){
			int min = Integer.MAX_VALUE;
			//Utilize greedy as evaluate function 
			if(depth <= 0){
				int x = greedyPick(board, new int[2]);
				return Math.min(min, -1*x*x);
			}
			
			int[][] copyBoard = getCopyOfBoard(board);
			Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
			getScoreMap(copyBoard, scoreMap);
			//System.out.println(scoreMap);
			int opScore = 0;
			int myScore = 0;
			for(int num : scoreMap.keySet()){
				opScore = scoreMap.get(num)*scoreMap.get(num);
			//	opScore = scoreMap.get(num);
				copyBoard = getCopyOfBoard(board);
				pick(copyBoard, num/columns, num%columns);
				fallDown(copyBoard);
			//	System.out.println("mindepth:"+depth+" "+num);
			//	Print(copyBoard);
				//System.out.println(isGameOver(copyBoard));
				myScore = 0;
				if(!isGameOver(copyBoard)){
					myScore = getScoreOfMax(copyBoard, depth-1, min);
				}
		//		System.out.println(myScore+" " +opScore);
				if(myScore - opScore <= a)
					return myScore - opScore;
				min = Math.min(min, myScore - opScore);
			}
		//	System.out.println(min);
			return min;
		}
		
		public int getScoreOfMax(int[][] board, int depth, int b){
			int max = Integer.MIN_VALUE;
			if(depth <= 0){
				int x = greedyPick(board, new int[2]);
				return Math.max(max, x*x);
			}
			int[][] copyBoard = getCopyOfBoard(board);
			Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
			getScoreMap(copyBoard, scoreMap);
			//System.out.println(scoreMap);
			int myScore = 0;
			int opScore = 0;
			for(int num : scoreMap.keySet()){
				myScore = scoreMap.get(num)*scoreMap.get(num);
			//	myScore = scoreMap.get(num);
				copyBoard = getCopyOfBoard(board);
				pick(copyBoard, num/columns, num%columns);
				fallDown(copyBoard);
			//	System.out.println("maxdepth:"+depth+" "+num);
			//	Print(copyBoard);
				if(!isGameOver(copyBoard)){
					opScore = getScoreOfMin(copyBoard, depth-1, max);
				}
				if(myScore+opScore >= b)
					return myScore+opScore;
				max = Math.max(max, myScore+opScore);
			}
			return max;
		}

	public boolean isGameOver(int[][] board){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				if(board[i][j] != -1)
					return false;
			}
		}
		return true;
	}
	
	// Look ahead the next move 
	public void forwardCheck(int[][] board, int[] m) {
		int[][] copyBoard = getCopyOfBoard(board);
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>(); 
		getScoreMap(copyBoard, scoreMap);
		int myScore = 0;
		int opScore = 0;
		int max = -rows*columns;
		int maxNum = 0;
		for (int n : scoreMap.keySet()) {
			myScore = scoreMap.get(n);
			copyBoard = getCopyOfBoard(board);
			pick(copyBoard, n/columns, n%columns);
			fallDown(copyBoard);
			opScore = greedyPick(copyBoard, m);
			if(myScore*myScore - opScore*opScore > max){
				max = myScore*myScore - opScore*opScore;
				maxNum = n;
			}
		}
		m[0] = maxNum/columns;
		m[1] = maxNum%columns;
	}
	
	public void randomPick(int[][] board, int[] m){
		Random rnd = new Random();
		int r = rows;
		int c = columns;
		int r1 = rnd.nextInt(r);
		int c1 = rnd.nextInt(c);
		while(board[r1][c1] == -1){
			r1 = rnd.nextInt(r);
			c1 = rnd.nextInt(c);
		}
		m[0] = r1;
		m[1] = c1;
	}
	
	public int greedyPick(int[][] board, int[] m){
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		getScoreMap(board, scoreMap);
		int max = -rows*columns;
		int key = 0;
		for(int num : scoreMap.keySet()){
			if(max < scoreMap.get(num)){
				max = scoreMap.get(num);
				key = num;
			}
		}
		m[0] = key/columns;
		m[1] = key%columns;
		return max;
	}
	// get score of each chosen node 
	public void getScoreMap(int[][] board, Map<Integer, Integer> scoreMap){
		List<Integer> block = new ArrayList<Integer>();
		int node = 0;
		for(int i = 0; i < rows*columns; i++){
			if(board[i/columns][i%columns] != -1){
				block.add(i);
			}
		}
		
		while(!block.isEmpty()){
			node = block.remove(0);
			int numOfFruit = pick(board, node/columns, node%columns);
			if(numOfFruit != 0){
				scoreMap.put(node, numOfFruit);
			}
		
		}
	}
	
	// pick a node and return the number of picked fruits 
	public int pick(int[][] board, int r, int c){
		int fruit = board[r][c];
		int numOfFruit = 0;
		if(fruit != -1){
			List<Integer> block = new ArrayList<Integer>();
			block.add(r*columns + c);
			while(!block.isEmpty()){
				int n = block.remove(0);
				int r1 = n/columns;
				int c1 = n%columns;
				if(board[r1][c1] != -1){
					numOfFruit ++;
					board[r1][c1] = -1;
					if(r1 < rows-1 && board[r1+1][c1] == fruit){
						block.add(n + columns);
					}if(r1 > 0 && board[r1-1][c1] == fruit){
						block.add(n - columns);
					}if(c1 < columns-1 && board[r1][c1+1] == fruit){
						block.add(n + 1);
					}if(c1 > 0 && board[r1][c1-1] == fruit){
						block.add(n - 1);
					}
				}
			}
		}
		return numOfFruit;
	}
	// fruits fall down due to gravity
	public void fallDown(int[][] board) {
		for(int i = 0; i < columns; i++){
			int k = rows-1;
			for(int j = rows-1; j >= 0; j--){
				if(board[j][i] != -1){
					board[k--][i] = board[j][i];
				}
			}
			for(;k >= 0; k--){
				board[k][i] = -1;
			}
		}
	}
	
	public int[][] getCopyOfBoard(int[][] board) {
		int[][] copyboard = new int[rows][columns];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				copyboard[i][j] = board[i][j];
		return copyboard;
	}
}
