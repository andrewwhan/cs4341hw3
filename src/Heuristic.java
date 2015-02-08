//Andrew Han, Alex Church

public class Heuristic 
{
	int value = 0;
	Board board;
	int[][] winningSpaceBoard;
	static int adjacencyMultiplier = 2;
	static int winningSpaceMultiplier = 7;
	static int centralMultiplier = 9;
	static int heightMultiplier = 1;
	static int bottomMultiplier = 3;
	static int popoutMultiplier = 5;
	
	public boolean terminalTest(Board boardstate){
		board = boardstate;
		winningSpaceBoard = new int[board.boardstate.length][board.boardstate[0].length];
		return win() || loss();
	}
	
	// Performs a slow, but in-depth, heuristic lookup
	public int getValue( Board boardstate)
	{
		board = boardstate;
		value = 0;
		winningSpaceBoard = new int[board.boardstate.length][board.boardstate[0].length];
		// gets the current board states then uses the helper methods to return a value of a move
//		 System.out.println("value at start:" + value);
		checkAdjacent();
//		 System.out.println("value after checkAdjacent:" + value);
		countWinningSpaces();
		central();
//		 System.out.println("value after central:" + value);
		bottom();
		popout();
		win();
		loss();
//		System.err.println(value);
		return value;
	}
	
	// Performs a fast, but simple, heuristic function
	public int getValueFast( Board boardstate)
	{
		board = boardstate;
		value = 0;
		winningSpaceBoard = new int[board.boardstate.length][board.boardstate[0].length];
		checkAdjacent();
		win();
		loss();
		//System.err.println(value);
		return value;
	}
	
	// Performs a medium complexity heuristic function
	public int getValueNormal( Board boardstate)
	{
		board = boardstate;
		value = 0;
		winningSpaceBoard = new int[board.boardstate.length][board.boardstate[0].length];
		checkAdjacent();
		central();
		bottom();
		win();
		loss();
		//System.err.println(value);
		return value;
	}
	
	private void checkAdjacent()
	{
		for(int i = 0; i < board.boardstate.length; i++)
		{
			for(int j = 0; j <board.boardstate[i].length; j++)
			{
				int chain = nInARow(board.boardstate, i,j);
				if(chain > 1){
					//Add or subtract to value based on whether it's their chain or our chain
					int usOrThem = (board.boardstate[i][j] == 1 ? 1 : -1);
					value += adjacencyMultiplier*chain*usOrThem;
				}			
			}
		}
	}
	
	private void countWinningSpaces(){
		for(int i=0; i<winningSpaceBoard.length; i++){
			for(int j=0; j<winningSpaceBoard[i].length; j++){
				if(winningSpaceBoard[i][j] == 1){
					value += 5*winningSpaceMultiplier;
					//System.err.println("Our winning space");
				}
				else if(winningSpaceBoard[i][j] == 2){
					value -= 5*winningSpaceMultiplier;
					//System.err.println("Their winning space");
				}
			}
		}
	}
	
	// Adds value to moves that are placed in a central location
	private void central()
	{
		int center = board.boardstate[0].length/2;
		for(int i = 0; i < board.boardstate.length; i++)
		{
			for(int j=0; j < board.boardstate[i].length; j++)
			{
				if(board.boardstate[i][j] != 0){
					int usOrThem = (board.boardstate[i][j] == 1 ? 1 : -1);
					value += usOrThem*centralMultiplier*(center - Math.abs(j-center));
					value += usOrThem*(heightMultiplier/2)*(board.boardstate.length - i);
				}
			}
		}
	}
	
	// Adds value to moves that put our pieces on the bottom row, as they can be popped out if needed
	private void bottom()
	{
		for(int i=0; i<board.boardstate[0].length; i++)
		{
			if(board.boardstate[0][i] == 1)
			{
				value += bottomMultiplier;
			}
			if(board.boardstate[0][i] == 2)
			{
				value -= bottomMultiplier;
			}
		}
	}
	
	private void popout(){
		if(board.ourPopout){
			value += 10 * popoutMultiplier;
		}
		if(board.theirPopout){
			value -= 10 * popoutMultiplier;
		}
	}
	
	private boolean win()
	{
		boolean won = false;
		for(int i=0; i<board.boardstate.length; i++){
			for(int j=0; j<board.boardstate[i].length; j++){
				if(board.boardstate[i][j] == 1){
					if(nInARow(board.boardstate, i, j) >= board.piecesToWin){
						value = 20000;
						won = true;
					}
				}
			}
		}
		/*If we won, prioritize moves where we close the game out the soonest, 
		 * to give our opponents less time to do something unexpected
		 */
		if(won){
			for(int i=0; i<board.boardstate.length; i++){
				for(int j=0; j<board.boardstate[i].length; j++){
					if(board.boardstate[i][j] == 1){
						value--;
					}
				}
			}
		}
		return won;
	}
	
	private boolean loss()
	{
		boolean lost = false;
		for(int i=0; i<board.boardstate.length; i++){
			for(int j=0; j<board.boardstate[i].length; j++){
				if(board.boardstate[i][j] == 2){
					if(nInARow(board.boardstate, i, j) >= board.piecesToWin){
						if(value > 10000){
							value += 20000;
						}
						else{
							value = -20000;
						}
						lost = true;
					}
				}
			}
		}
		/* If we lost, prioritize moves where we stall out the game,
		 * to give our opponents more time to make a mistake
		 */
		if(lost){
			for(int i=0; i<board.boardstate.length; i++){
				for(int j=0; j<board.boardstate[i].length; j++){
					if(board.boardstate[i][j] == 2){
						value++;
					}
				}
			}
		}
		return lost;
	}
	
	//Given the location of a piece, returns the length of the longest chain of pieces that particular piece is part of.
	public int nInARow(int[][] boardstate, int row, int column){
		int player = boardstate[row][column];
		if(player == 0){
			return 0;
		}
		
		int maxChain = 1;
		int currentChain = 1;
		int[][] winningSpaces = new int[6][2];
		int numSpaces = 0;
		int[] openSpace = new int[2];
		openSpace[0] = -1;
		openSpace[1] = -1;
		
		//Left-Right Chain
		//Check left
		int x = column-1;
		int y = row;
		while(x > -1 && boardstate[y][x] == player){
			currentChain++;
			x--;
		}
		if(x > -1 && boardstate[y][x] == 0){
			openSpace[0] = x;
			openSpace[1] = y;
		}
		//Upon reaching terminating piece, space, or edge of the board, check to the right
		x = column+1;
		while(x < boardstate[y].length && boardstate[y][x] == player){
			currentChain++;
			x++;
		}
		if(currentChain == board.piecesToWin - 1){
			if(openSpace[0] != -1){
				winningSpaces[numSpaces][0] = openSpace[0];
				winningSpaces[numSpaces][1] = openSpace[1];
				numSpaces++;
				openSpace[0] = -1;
				openSpace[1] = -1;
			}
			if(x < boardstate[y].length && boardstate[y][x] == 0){
				winningSpaces[numSpaces][0] = x;
				winningSpaces[numSpaces][1] = y;
				numSpaces++;
			}
		}
		//Record left-right chain as the max chain
		maxChain = currentChain;
		
		
		//Up-Down Chain
		//Check up
		currentChain = 1;
		x = column;
		y = row + 1;
		while(y < boardstate.length && boardstate[y][x] == player){
			currentChain++;
			y++;
		}
		if(y < boardstate.length && boardstate[y][x] == 0){
			openSpace[0] = x;
			openSpace[1] = y;
		}
		//Upon reaching terminating piece, space, or edge of the board, check down
		y = row - 1;
		while(y > -1 && boardstate[y][x] == player){
			currentChain++;
			y--;
		}
		if(currentChain == board.piecesToWin - 1){
			if(openSpace[0] != -1){
				winningSpaces[numSpaces][0] = openSpace[0];
				winningSpaces[numSpaces][1] = openSpace[1];
				numSpaces++;
				openSpace[0] = -1;
				openSpace[1] = -1;
			}
			if(y > -1 && boardstate[y][x] == 0){
				winningSpaces[numSpaces][0] = x;
				winningSpaces[numSpaces][1] = y;
				numSpaces++;
			}
		}
		//If we have a new maxChain record it
		if(currentChain > maxChain){
			maxChain = currentChain;
		}
		
		
		//UpLeft-DownRight Chain
		//Check up-left
		currentChain = 1;
		x = column - 1;
		y = row + 1;
		while(y < boardstate.length && x > -1 && boardstate[y][x] == player){
			currentChain++;
			x--;
			y++;
		}
		if(y < boardstate.length && x > -1 && boardstate[y][x] == 0){
			openSpace[0] = x;
			openSpace[1] = y;
		}
		//Upon reaching terminating piece, space, or edge of the board, check down-right
		x = column + 1;
		y = row - 1;
		while(y > -1 && x < boardstate[y].length && boardstate[y][x] == player){
			currentChain++;
			x++;
			y--;
		}
		if(currentChain == board.piecesToWin - 1){
			if(openSpace[0] != -1){
				winningSpaces[numSpaces][0] = openSpace[0];
				winningSpaces[numSpaces][1] = openSpace[1];
				numSpaces++;
				openSpace[0] = -1;
				openSpace[1] = -1;
			}
			if(y > -1 && x < boardstate[y].length && boardstate[y][x] == 0){
				winningSpaces[numSpaces][0] = x;
				winningSpaces[numSpaces][1] = y;
				numSpaces++;
			}
		}
		//If we have a new maxChain record it
		if(currentChain > maxChain){
			maxChain = currentChain;
		}
		
		
		//UpRight-DownLeft Chain
		//Check up-right
		currentChain = 1;
		x = column + 1;
		y = row + 1;
		while(y < boardstate.length && x < boardstate[y].length && boardstate[y][x] == player){
			currentChain++;
			x++;
			y++;
		}
		if(y < boardstate.length && x < boardstate[y].length && boardstate[y][x] == 0){
			openSpace[0] = x;
			openSpace[1] = y;
		}
		//Upon reaching terminating piece, space, or edge of the board, check down-left
		x = column - 1;
		y = row - 1;
		while(y > -1 && x > -1 && boardstate[y][x] == player){
			currentChain++;
			x--;
			y--;
		}
		if(currentChain == board.piecesToWin - 1){
			if(openSpace[0] != -1){
				winningSpaces[numSpaces][0] = openSpace[0];
				winningSpaces[numSpaces][1] = openSpace[1];
				numSpaces++;
				openSpace[0] = -1;
				openSpace[1] = -1;
			}
			if(y > -1 && x > -1 && boardstate[y][x] == 0){
				winningSpaces[numSpaces][0] = x;
				winningSpaces[numSpaces][1] = y;
				numSpaces++;
			}
		}
		//If we have a new maxChain record it
		if(currentChain > maxChain){
			maxChain = currentChain;
		}
		
		for(int i=0; i<numSpaces; i++){
			winningSpaceBoard[winningSpaces[i][1]][winningSpaces[i][0]] = player;
		}
		
		return maxChain;
	}
	
}