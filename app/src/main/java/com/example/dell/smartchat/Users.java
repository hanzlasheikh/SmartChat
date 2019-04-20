package com.example.dell.smartchat;

public class Users {

    public String name;
    public String image;

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String thumb_image;
    public String status;
    public Users(){

    }

    public Users(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_image=thumb_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
