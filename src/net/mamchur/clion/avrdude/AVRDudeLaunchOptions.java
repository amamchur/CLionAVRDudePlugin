package net.mamchur.clion.avrdude;

class AVRDudeLaunchOptions {
    private static final String MemTypeFlash = "flash";

    private String elfFile;

    AVRDudeLaunchOptions(String elfFile) {
        this.elfFile = elfFile;
    }

    String getUploadFlashParam() {
        StringBuilder builder = new StringBuilder();
        builder.append("-U").append(MemTypeFlash);
        builder.append(":w").append(":").append(elfFile).append(":e");
        return builder.toString();
    }

    public String getElfFile() {
        return elfFile;
    }
}
