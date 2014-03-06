import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.util.concurrent.*;
import javax.net.*;

import java.awt.image.*;
import javax.imageio.*;

import java.nio.file.*;

import com.example.cookbuddy.*;

public class Server extends Thread {
  private ServerSocket serverSocket;
  private ExecutorService pool = Executors.newCachedThreadPool();

  private final static String MAIN_FILE = "main.txt";

  public static void main(String[] args) {
    (new Server()).run();
  }

  public Server() {
    try {
      serverSocket = ServerSocketFactory.getDefault().createServerSocket(10000);

      if (serverSocket == null) {
        System.out.println("Nu e bine\n");
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void run() {
    while (true) {
      try {
        Socket s = serverSocket.accept();

        System.out.println("Am primit ceva ok");
        pool.execute(new ClientThread(s));
      } catch (IOException ex) {}
    }
  }

  private class ClientThread extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientThread(Socket s) {
      socket = s;

      try {
        out = new ObjectOutputStream(s.getOutputStream());
        in = new ObjectInputStream(s.getInputStream());
      } catch (IOException ex) {}
    }

    public ArrayList<ShortRecipe> getRecipeList() {
      ArrayList<ShortRecipe> recipes = new ArrayList<>();

      try {
        BufferedReader input = new BufferedReader(
          new InputStreamReader(new FileInputStream(MAIN_FILE)));

        int nrRecipes = Integer.parseInt(input.readLine());
        System.out.println(nrRecipes);
        for (int i = 0; i < nrRecipes; i++) {
          ShortRecipe curRecipe = new ShortRecipe();

          curRecipe.title = input.readLine();
          curRecipe.id = input.readLine();
          curRecipe.complexity = Integer.parseInt(input.readLine());
          curRecipe.duration = input.readLine();
          curRecipe.categories = input.readLine();

          curRecipe.picture = getByteFromFile(curRecipe.id + "/main");

          System.out.println("e ok" + curRecipe.picture.length);

          recipes.add(curRecipe);
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      return recipes;
    }

    private byte[] getByteFromFile(String name) throws IOException {
      DataInputStream dis = null;
      File file = null;
      try {
        file = new File(name + ".png");
        dis = new DataInputStream(new FileInputStream(file));
      } catch (FileNotFoundException ex) {
        file = new File(name + ".jpg");
        dis = new DataInputStream(new FileInputStream(file));
      }

      byte[] rez = new byte[(int) file.length()];
      dis.readFully(rez);

      return rez;
    }

    public CompleteRecipe getParticularRecipe(String id) {
      File recipeFile = new File(id + "/recipe.txt");

      CompleteRecipe curRecipe = new CompleteRecipe(id);

      try {
        BufferedReader input = new BufferedReader(
                  new InputStreamReader(new FileInputStream(recipeFile)));

        curRecipe.nrSteps = Integer.parseInt(input.readLine());

        //System.out.println(nrSteps);

        for (;;) {
          String ingredient = input.readLine();

          if (ingredient == null) {
            break;
          }

          curRecipe.ingredients.add(ingredient);
        }

        input.close();

        System.out.println("dadada");
        curRecipe.mainPicture = getByteFromFile(curRecipe.id + "/header");
        curRecipe.steps = new ArrayList<String>();
        curRecipe.pictures = new ArrayList<byte[]>();

        System.out.println("dupa2");
        for (int i = 1; i <= curRecipe.nrSteps; i++) {
          System.out.println(i);
          File stepFile = new File(curRecipe.id + "/step" + i + ".txt");
          input = new BufferedReader(
                    new InputStreamReader(new FileInputStream(stepFile)));

          String step = "";

          for (;;) {
            String line = input.readLine();

            if (line == null) {
              break;
            }

            step += line;
          }

          System.out.println(i);
          curRecipe.steps.add(step);
          curRecipe.pictures.add(getByteFromFile(curRecipe.id + "/step" + i));

          System.out.println(i);
        }
      } catch (IOException ex) {}

      return curRecipe;
    }

    public void run() {
      while (true) {
        try {
          DataRequest request = (DataRequest)in.readObject();

          switch (request.type) {
            case DataRequest.MAIN_RECIPES: {
              System.out.println("Da");
              out.writeObject(getRecipeList());
              out.flush();
              break;
            }
            case DataRequest.RECIPE: {
              System.out.println("Da2");
              out.writeObject(getParticularRecipe(request.id));
              out.flush();

              System.out.println(request.id);
              break;
            }
          }
        } catch (Exception ex) {
          break;
        }
      }
    }
  }
}