package audio;

/**
 * This class was developed based on various entries on "stackoverflow" such as
 * @see https://stackoverflow.com/questions/25798200/java-record-mic-to-byte-array-and-play-sound
 * @see https://stackoverflow.com/questions/4708613/graphing-the-pitch-frequency-of-a-sound
 * 
 */

import interaction.MouseInteraction;
import java.awt.AWTException;
import javax.sound.sampled.*;

public class AudioClicker {

    private static int index = 1;
    private static int columnSize;

    public static void main(final String[] args) throws Exception {

        // we take only 1 channel:
        final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
        final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        final TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
        targetLine.open();
        targetLine.start();
        final AudioInputStream audioStream = new AudioInputStream(targetLine);
        
        // fixed-size buffer
        final byte[] buf = new byte[256];
        final int numberOfSamples = buf.length / format.getFrameSize();
        MouseInteraction mi = new MouseInteraction();
        int counter = 0;

        while (true) {
            byte[] bytes = new byte[numberOfSamples];
            audioStream.read(bytes);

            int[][] graphData = getUnscaledAmplitude(bytes, 1);
            double threshold = getThreshold(graphData);

            if (tapDetected(graphData, threshold)) {
                counter++;

                if (counter >= 3) {
                    System.out.println(index + " -> Click");

                    try {
                        mi.click();
                    } catch (AWTException ex) {
                        System.out.println(ex.getMessage());
                    }

                    index++;
                    counter = 0;

                }// end of if

            }// end of if

        }// end of while
    }

    private static boolean tapDetected(int[][] data, double threshold) {
        if (threshold == 0) {
            return false;
        }

        boolean result = false;
        int count = 0;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] > threshold) {
                    count++;
                }
            }
        }

        double cnt = new Double(count);
        double whl = new Double(columnSize);
        double ratio = cnt / whl;

        // this is an empirically-defined value:
        if (ratio < 0.05) {
            result = true;
        }

        return result;
    }

    private static int getMaxValue(int[][] data) {
        int max = 0;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] > max) {
                    max = data[i][j];
                }
            }
        }
        return max;
    }

    private static double getThreshold(int[][] data) {
        int max = getMaxValue(data);
        double threshold = 0.0;

        if (max >= 10) {
            threshold = max * 0.75;
        }
        return threshold;
    }

    /**
     * @see https://stackoverflow.com/questions/4708613/graphing-the-pitch-frequency-of-a-sound
     * @param eightBitByteArray
     * @param nbChannels
     * @return 
     */
    private static int[][] getUnscaledAmplitude(byte[] eightBitByteArray, int nbChannels) {
        columnSize = eightBitByteArray.length / (2 * nbChannels);

        int[][] toReturn = new int[nbChannels][columnSize];
        int index = 0;

        for (int audioByte = 0; audioByte < eightBitByteArray.length;) {
            for (int channel = 0; channel < nbChannels; channel++) {
                // Do the byte to sample conversion.
                int low = (int) eightBitByteArray[audioByte];
                audioByte++;
                int high = (int) eightBitByteArray[audioByte];
                audioByte++;
                int sample = (high << 8) + (low & 0x00ff);

                toReturn[channel][index] = sample;
            }
            index++;
        }

        return toReturn;
    }

}
