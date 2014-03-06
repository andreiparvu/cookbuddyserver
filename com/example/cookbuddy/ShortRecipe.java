package com.example.cookbuddy;
import java.io.*;

public class ShortRecipe implements Serializable {
  public String title, id, duration;
  public int complexity;
  public String categories;
  public byte[] picture;
}


