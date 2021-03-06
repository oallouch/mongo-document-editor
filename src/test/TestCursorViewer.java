package test;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.oallouch.mongodoc.cursorview.CursorViewer;
import com.oallouch.mongodoc.output.JsonArea;
import java.net.InetAddress;
import java.util.Base64;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TestCursorViewer extends Application {

	@Override
	public void start(Stage primaryStage) {
		DBCursor cursor;
		try {
			String computerName = InetAddress.getLocalHost().getHostName();
			
			/*String clearServerIP = "clear server ip";
			byte[] encoded = xor(clearServerIP.getBytes("ISO-8859-1"), computerName.getBytes("UTF-8"));
			String encodedStr = new String(Base64.getEncoder().encode(encoded));
			System.out.println("encodedStr: " + encodedStr);*/
			
			String input = "Z3lgcGR0fX5+dXpzYH0=";
			byte[] inputDecoded = Base64.getDecoder().decode(input);
			String serverIP = new String(xor(inputDecoded, computerName.getBytes("UTF-8")), "ISO-8859-1");
			System.out.println("serverIP: " + serverIP);
			MongoClient mongo = new MongoClient(serverIP, MongoClientOptions.builder().readPreference(ReadPreference.secondaryPreferred()).build());
			DBCollection collection = mongo.getDB("gallery").getCollection("galleries");
			cursor = collection.find().limit(50);
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
		
		
		CursorViewer cursorViewer = new CursorViewer(cursor, 50);
		cursor.close();


		BorderPane root = new BorderPane();
		root.setCenter(cursorViewer);
		root.setBottom(new Label("bottom part to check the clipping rectangle"));

		Scene scene = new Scene(root, 1000, 500);
		scene.getStylesheets().add("com/oallouch/mongodoc/DocumentEditor.css");

		primaryStage.setTitle("Cursor Viewer Test");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private static byte[] xor(final byte[] input, final byte[] secret) {
		final byte[] output = new byte[input.length];
		if (secret.length == 0) {
			throw new IllegalArgumentException("empty security key");
		}
		int spos = 0;
		for (int pos = 0; pos < input.length; ++pos) {
			output[pos] = (byte) (input[pos] ^ secret[spos]);
			++spos;
			if (spos >= secret.length) {
				spos = 0;
			}
		}
    return output;
}

	@Override
	public void stop() throws Exception {
		JsonArea.shutdown();
	}
	

	public static void main(String[] args) {
		launch(args);
	}

}