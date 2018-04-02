package net.mamchur.clion.avrdude;

class AVRDudeLaunchOptions {
    static final String MemTypeFlash = "flash";

    private String programmer = "usbasp";
    private String binFile;
    private String mcu;

    AVRDudeLaunchOptions(String binFile, String mcu) {
        this.binFile = binFile;
        this.mcu = mcu;
    }

    String getUploadFlashParam() {
        StringBuilder builder = new StringBuilder();
        builder.append("-U");
        builder.append(MemTypeFlash);
        builder.append(":w");
        builder.append(":" + binFile);
        builder.append(":r");
        return builder.toString();
    }

    public String getBinFile() {
        return binFile;
    }

    public String getMcu() {
        return mcu;
    }

    public String getProgrammer() {
        return programmer;
    }
}
