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
			if(!isGameOver(new_board)){
				opScore = getScoreOfMin(new_board, depth-1, maxScore);
			}
			if(maxScore < myScore + opScore){
				maxScore = myScore + opScore;
				maxNum = num;
							
			}
		}
		m[0] = maxNum/columns;
		m[1] = maxNum%columns; 
	}
	public static void minmaxPick(int[][] board, int[] m){
		int[][] copyBoard = getCopyOfBoard(board);
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		int[][] sort = getScoreMap(copyBoard, scoreMap);
		int depth = 2;
		// Adjust the forward checking depth according to board size
		if(scoreMap.size() < 10){ 
			depth = 8;
		}else if(scoreMap.size() < 30){
			depth = 6;
		}else if(scoreMap.size() < 135){
			depth = 4;
		}
		depth = 2;
		System.out.println(depth);
		int myScore = 0;
		int opScore = 0;
		int maxScore =  -rows*columns*rows*columns;
		int maxNum = 0;
		for(int i = 0; i < sort.length; i++){
			int num = sort[i][0];
			int isGameOver = remaining;
			copyBoard = getCopyOfBoard(board);
			myScore = scoreMap.get(num)*scoreMap.get(num);
			opScore = 0;
			isGameOver -= pick(copyBoard, num/columns, num%columns);
			fallDown(copyBoard);
			//temp record the remaining number of fruits if it's 0 then game over 
			if(isGameOver > 0){
				opScore = getScoreOfMin(copyBoard, depth-1, maxScore - myScore, isGameOver);
			}
			if(maxScore < myScore + opScore){
				maxScore = myScore + opScore;
				maxNum = num;			
			}
		}
		m[0] = maxNum/columns;
		m[1] = maxNum%columns; 
	}
	
	public static int getScoreOfMin(int[][] board, int depth, int a, int numOfRemain){
		int min =  rows*columns*rows*columns;
		//Utilize greedy as evaluate function 
		if(depth <= 0){
			int x = greedyPick(board, new int[2]);
			return Math.min(min, -1*x*x);
		}
		int[][] copyBoard = getCopyOfBoard(board);
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		int[][] sort = getScoreMap(copyBoard, scoreMap);
		int opScore = 0;
		int myScore = 0;
		for(int i = 0; i < sort.length; i++){
			int num = sort[i][0];
			int isGameOver =numOfRemain;
			if(!scoreMap.containsKey(num))
				System.out.println(num);
			opScore = scoreMap.get(num)*scoreMap.get(num);
			copyBoard = getCopyOfBoard(board);
			isGameOver -= pick(copyBoard, num/columns, num%columns);
			fallDown(copyBoard);
			myScore = 0;
			if(isGameOver > 0){
				myScore = getScoreOfMax(copyBoard, depth-1, min + opScore, isGameOver);
			}
			if(myScore - opScore <= a)
				return myScore - opScore;
			min = Math.min(min, myScore - opScore);
		}
		return min;
	}
	
	//Get the score of max layer
	public static int getScoreOfMax(int[][] board, int depth, int b, int numOfRemain){
		int max = -rows*columns*rows*columns;
		//Utilize greedy as evaluate function 
		if(depth <= 0){
			int x = greedyPick(board, new int[2]);
			return Math.max(max, x*x);
		}
		int[][] copyBoard = getCopyOfBoard(board);
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		int[][] sort = getScoreMap(copyBoard, scoreMap);
		int myScore = 0;
		int opScore = 0;
		for(int i = 0; i < sort.length; i++){
			int num = sort[i][0];
			int isGameOver = numOfRemain;
			myScore = scoreMap.get(num)*scoreMap.get(num);
			copyBoard = getCopyOfBoard(board);
			isGameOver -= pick(copyBoard, num/columns, num%columns);
			fallDown(copyBoard);
			opScore = 0;
			// Maximize myScore - opScore, pruning if the result is larger than b
			if(isGameOver > 0){
				opScore = getScoreOfMin(copyBoard, depth-1, max - myScore, isGameOver);
			}
			if(myScore+opScore >= b)
				return myScore+opScore;
			max = Math.max(max, myScore+opScore);
		}
		return max;
	}
	
	// Look ahead the next move 
	public static void forwardCheck(int[][] board, int[] m) {
		int[][] copyBoard = getCopyOfBoard(board);
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>(); 
		int[][] sort = getScoreMap(copyBoard, scoreMap);
		int myScore = 0;
		int opScore = 0;
		int max = -rows*columns*rows*columns;
		int maxNum = 0;
		for(int i = 0; i < sort.length; i++){
			int num = sort[i][0];
			myScore = scoreMap.get(num);
			copyBoard = getCopyOfBoard(board);
			int isGameOver = remaining - pick(copyBoard, num/columns, num%columns);
			fallDown(copyBoard);
			if(isGameOver > 0)
				opScore = greedyPick(copyBoard, m);
			if(myScore*myScore - opScore*opScore > max){
				max = myScore*myScore - opScore*opScore;
				maxNum = num;
			}
		}
		m[0] = maxNum/columns;
		m[1] = maxNum%columns;
	}
	
	public static void randomPick(int[][] board, int[] m){
		// pick randomly is time is out. O(1)
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
	
	public static int greedyPick(int[][] board, int[] m){
		// choose the node with max. O(n*n)
		Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		int[][] sort = getScoreMap(board, scoreMap);
		int key = 0;
		key = sort[0][0];
		m[0] = key/columns;
		m[1] = key%columns;
		return sort[0][1];
	}
	
	// get score of each chosen node and store them. O(n*n)
	public static int[][] getScoreMap(int[][] board, Map<Integer, Integer> scoreMap){
		List<Integer> block = new ArrayList<Integer>();
		int node = 0;
		for(int i = 0; i < rows*columns; i++){
			if(board[i/columns][i%columns] != -1){
				block.add(i);
			}
		}
		while(!block.isEmpty()){
			node = block.remove(0);
			if(board[node/columns][node%columns] != -1){
				int numOfFruit = pick(board, node/columns, node%columns);
				scoreMap.put(node, numOfFruit);
			}
		}
		int[][] sort = new int[scoreMap.size()][2];
		int i = 0;
		for(int key : scoreMap.keySet()){
			sort[i][0] = key;
			sort[i][1] = scoreMap.get(key);
			i++;
		}
		Arrays.sort(sort, (a,b) -> b[1]- a[1]);
		return sort;
	}
	
	// pick a node and return the number of picked fruits 
	public static int pick(int[][] board, int r, int c){
		int fruit = board[r][c];
		int numOfFruit = 0;
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
		return numOfFruit;
	}
	
	// fruits fall down due to gravity
	public static void fallDown(int[][] board) {
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
	
	public static int[][] getCopyOfBoard(int[][] board) {
		int[][] copyboard = new int[rows][columns];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				copyboard[i][j] = board[i][j];
		return copyboard;
	}
}
