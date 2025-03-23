package com.jldubz.gistaviewer.model.gists;

public class GistFile {
    /*
    Modified sample data from: https://developer.github.com/v3/gists/#list-a-users-gists
    {
        "filename": "hello_world.rb"
    }
    */

    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
