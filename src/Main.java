import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class Main {
	public static void main(String[] args) throws IOException{
		BufferedReader in;
		if(args.length > 0){
			in = new BufferedReader(new FileReader(new File(args[0])));
		}
		else{
			return;
		}
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("features.csv", true)));
		out.println("a1,a2,a3,a4,a5,a6,b1,b2,b3,b4,b5,b6,c1,c2,c3,c4,c5,c6,d1,d2,d3,d4,d5,d6,e1,e2,e3,e4,e5,e6,f1,f2,f3,f4,f5,f6,g1,g2,g3,g4,g5,g6,result,adjacencyScore,winningSpaces,centralScore,bottom,heuristic");
		while(in.ready()){
			String features = in.readLine();
			Board board = new Board(6, 7, 4);
			Scanner s = new Scanner(features);
			s.useDelimiter(",");
			for(int w=0; w<7; w++){
				for(int h=0; h<6; h++){
					board.boardstate[h][w] = s.nextInt();
				}
			}
			s.close();
			int[] values = new Heuristic().getValue(board);
			for(int i:values){
				features += "," + i;
			}
			out.println(features);

		}
		in.close();
		out.close();
	}
}
