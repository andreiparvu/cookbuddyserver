package com.example.cookbuddy;
import java.io.*;
import java.util.*;

public class CompleteRecipe implements Serializable {
  public String id;
  public int nrSteps;
  public ArrayList<String> ingredients = new ArrayList<String>(),
                           steps = new ArrayList<String>();
  public ArrayList<byte[]> pictures = new ArrayList<byte[]>();
  public byte[] mainPicture;

  public CompleteRecipe(String id) {
    this.id = id;
  }
}