package com.example.cookbuddy;
import java.io.*;

public class DataRequest implements Serializable {
  public final static int MAIN_RECIPES = 1;
  public final static int RECIPE = 2;

  public int type;
  public String id;

  public DataRequest(int type) {
    this.type = type;
  }
}


