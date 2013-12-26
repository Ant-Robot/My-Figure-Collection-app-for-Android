
package net.myfigurecollection.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Utils {

    public static String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the
		 * Reader.read(char[] buffer) method. We iterate until the
		 * Reader return -1 which means there's no more data to
		 * read. We use the StringWriter class to produce the string.
		 */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        }
        return "";
    }

    public static boolean isOnline(Activity ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    /**
     * @return an encoded String
     */
    public static String md5(String sessionid) {
        byte[] defaultBytes = sessionid.getBytes();
        String md5 = "";

        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                hexString.append(Integer.toHexString(0xFF & aMessageDigest));
            }
            // String foo = messageDigest.toString();
            System.out.println("sessionid " + sessionid + " md5 version is " + hexString.toString());
            md5 = hexString.toString();
        } catch (NoSuchAlgorithmException ignored) {

        }
        return md5;
    }

    public static Bitmap getBitmapFromFile(String filePath, int maxHeight,
                                           int maxWidth) {
        return getBitmapFromFile(filePath, maxHeight, maxWidth, null);
    }

    public static Bitmap getBitmapFromFile(String filePath, int maxHeight, int maxWidth, Bitmap.Config config) {
        // check dimensions for sample size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // calculate sample size
        options.inSampleSize = getSampleSize(options, maxHeight, maxWidth);

        if (config != null) options.inPreferredConfig = config;

        // decode Bitmap with sample size
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int getSampleSize(BitmapFactory.Options options, int maxHeight, int maxWidth) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int sampleSize = 1;

        if (height > maxHeight || width > maxWidth) {
            // calculate ratios of given height/width to max height/width
            final int heightRatio = Math.round((float) height / (float) maxHeight);
            final int widthRatio = Math.round((float) width / (float) maxWidth);

            // select smallest ratio as the sample size
            if (heightRatio > widthRatio)
                return heightRatio;
            else
                return widthRatio;
        } else
            return sampleSize;
    }
}
