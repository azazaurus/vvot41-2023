package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FaceCropHelper {
    public InputStream crop(InputStream photo, Coordinates coordinates) {
        ArrayList<InputStream> facesPhoto = new ArrayList<>();
        BufferedImage bufferedImage = null;
        InputStream facePhoto;
        int x = coordinates.leftTop[0];
        int y = coordinates.leftTop[1];
        int w = coordinates.rightTop[0] - coordinates.leftTop[0];
        int h = coordinates.rightTop[1] - coordinates.rightBottom[1];
        try {
            bufferedImage = ImageIO.read(photo);
            BufferedImage faceImage = bufferedImage.getSubimage(x, y, w, h);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(faceImage, "jpg", os);
            facePhoto = new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return facePhoto;
    }
}
