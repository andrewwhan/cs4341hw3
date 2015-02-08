//Andrew Han, Alex Church

public class Board {
	
	int[][] boardstate;
	int piecesToWin;
	boolean ourPopout;
	boolean theirPopout;
	
	Board(int height, int width, int piecesToWin){
		boardstate = new int[height][width];
		this.piecesToWin = piecesToWin;
		//printBoard();
	}
	
	//Copy constructor
	Board(Board copyBoard){
		boardstate = copyBoard.boardstate.clone();
		for(int i=0; i<copyBoard.boardstate.length; i++){
			boardstate[i] = copyBoard.boardstate[i].clone();
		}
		piecesToWin = copyBoard.piecesToWin;
	}
	
	public void makeMove(int player, int column, int movetype)
	{
		if(movetype == 1)
		{
			if(!columnFull(column)){
				int newheight = 0;
				while(boardstate[newheight][column] > 0 && newheight < boardstate.length)
				{
					newheight ++;
				}
				boardstate[newheight][column] = player;
			}
		}
		
		if(movetype == 0)
		{
			for( int i=0; i < boardstate.length-1; i++)
			{
				boardstate[i][column] = boardstate[i+1][column];
			}
			boardstate[boardstate.length-1][column] = 0;
			if(player == 1){
				ourPopout = false;
			}
			else{
				theirPopout = false;
			}
		}
		//printBoard();
	}
	
	//Print the board
	public void printBoard(){
		for(int i=boardstate.length-1; i>=0; i--){
			for(int j=0; j<boardstate[i].length; j++){
				System.err.print(boardstate[i][j]);
			}
			System.err.println();
		}
		System.err.println();
	}
	
	//Checks if the given column is full
	public boolean columnFull(int column){
		return boardstate[boardstate.length-1][column] != 0;
	}
	
	//Used to phantom play moves and see what the resulting board state would be.
	public Board tryMove(int player, int column, int movetype){
		//System.err.println("Trying move");
		Board returnBoard = new Board(this);
		returnBoard.makeMove(player, column, movetype);
		return returnBoard;
	}
	
	public boolean validMove(int player, int column, int movetype){
		if(movetype == 1 && columnFull(column)){
			return false;
		}
		if(movetype == 0 && (boardstate[0][column] != player)){
			return false;
		}
		if(movetype == 0 && player == 1 && !ourPopout){
			return false;
		}
		if(movetype == 0 && player == 2 && !theirPopout){
			return false;
		}
		
		return true;
	}
}
