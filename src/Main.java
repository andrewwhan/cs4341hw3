import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Main {
	public static void main(String[] args) throws IOException{
		BufferedReader in;
		if(args.length > 0){
			in = new BufferedReader(new FileReader(new File(args[0])));
		}
		else{
			return;
		}
		while(in.ready()){
			String features = in.readLine();
			features += ",a";
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("features.csv", true)));
			out.println(features);
			out.close();
		}
		in.close();
	}
}
