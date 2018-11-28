package com.example.inceptive.imageadapter.ModelClass;

public class ImageModel {


private int id;
    private String name;
    private byte[] image;


    public ImageModel(int id, String name, byte[] image) {
        this.id=id;
        this.name = name;
        this.image=image;
    }

    public ImageModel(int id, byte[] image) {
        this.id=id;
        this.image=image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
