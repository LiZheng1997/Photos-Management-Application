package uk.ac.shef.oak.com6510;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.util.List;

/**
 * <h1>Store groups of photos on same date</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class ImageOnDate {

    List<ImageElement> imageElements;

    public ImageOnDate(List<ImageElement> imageElements) {
        this.imageElements = imageElements;
    }

    public List<ImageElement> getImageElements() {
        return imageElements;
    }

    /**
     * Set list of ImageElements
     * @param imageElements
     */
    public void setImageElements(List<ImageElement> imageElements) {
        this.imageElements = imageElements;
    }

    @Override
    public String toString() {
        return "ImageOnDate{" +
                "imageElements Size=" + imageElements.size() + ":" + imageElements +
                '}';
    }
}
