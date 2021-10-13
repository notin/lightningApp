package com.lightning.notification.app.utils;

import org.apache.commons.lang3.tuple.MutablePair;

public class TileSystem
{
    private static double earthRadius = 6378137;
    private static double minLatitude = -85.05112878;
    private static double maxLatitude = 85.05112878;
    private static double minLongitude = -180;
    private static double maxLongitude = 180;

    /// <summary>  
    /// Clips a number to the specified minimum and maximum values.  
    /// </summary>  
    /// <param name="n">The number to clip.</param>  
    /// <param name="minValue">Minimum allowable value.</param>  
    /// <param name="maxValue">Maximum allowable value.</param>  
    /// <returns>The clipped value.</returns>  
    private static double Clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    /// <summary>  
    /// Determines the map width and height (in pixels) at a specified level  
    /// of detail.  
    /// </summary>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <returns>The map width and height in pixels.</returns>  
    public static Integer MapSize(Integer levelOfDetail)
    {
        return (Integer) 256 << levelOfDetail;
    }

    /// <summary>  
    /// Determines the ground resolution (in meters per pixel) at a specified  
    /// latitude and level of detail.  
    /// </summary>  
    /// <param name="latitude">Latitude (in degrees) at which to measure the  
    /// ground resolution.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <returns>The ground resolution, in meters per pixel.</returns>  
    public static double GroundResolution(double latitude, Integer levelOfDetail)
    {
        latitude = Clip(latitude, minLatitude, maxLatitude);
        return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * earthRadius / MapSize(levelOfDetail);
    }

    /// <summary>  
    /// Determines the map scale at a specified latitude, level of detail,  
    /// and screen resolution.  
    /// </summary>  
    /// <param name="latitude">Latitude (in degrees) at which to measure the  
    /// map scale.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <param name="screenDpi">Resolution of the screen, in dots per inch.</param>  
    /// <returns>The map scale, expressed as the denominator N of the ratio 1 : N.</returns>  
    public static double MapScale(double latitude, Integer levelOfDetail, Integer screenDpi)
    {
        return GroundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
    }

    /// <summary>  
    /// Converts a poInteger from latitude/longitude WGS-84 coordinates (in degrees)  
    /// into pixel XY coordinates at a specified level of detail.  
    /// </summary>  
    /// <param name="latitude">Latitude of the point, in degrees.</param>  
    /// <param name="longitude">Longitude of the point, in degrees.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <param name="pixelX">Output parameter receiving the X coordinate in pixels.</param>  
    /// <param name="pixelY">Output parameter receiving the Y coordinate in pixels.</param>  
    public static MutablePair<Integer, Integer> latLongToPixelXY(double latitude, double longitude, Integer levelOfDetail)
    {
        latitude = Clip(latitude, minLatitude, maxLatitude);
        longitude = Clip(longitude, minLongitude, maxLongitude);

        double x = (longitude + 180) / 360;
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        Integer mapSize = MapSize(levelOfDetail);
        Integer pixelX = Integer.valueOf((int) Clip(x * mapSize + 0.5, 0, mapSize - 1));
        Integer pixelY = Integer.valueOf((int) Clip(y * mapSize + 0.5, 0, mapSize - 1));
        return new MutablePair<>(pixelX,pixelY);
    }
  
    /// <summary>  
    /// Converts a pixel from pixel XY coordinates at a specified level of detail  
    /// into latitude/longitude WGS-84 coordinates (in degrees).  
    /// </summary>  
    /// <param name="pixelX">X coordinate of the point, in pixels.</param>  
    /// <param name="pixelY">Y coordinates of the point, in pixels.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <param name="latitude">Output parameter receiving the latitude in degrees.</param>  
    /// <param name="longitude">Output parameter receiving the longitude in degrees.</param>  
    public static MutablePair<Double, Double> PixelXYToLatLong(Integer pixelX, Integer pixelY, Integer levelOfDetail,  double latitude,  double longitude)
    {
        double mapSize = MapSize(levelOfDetail);
        double x = (Clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (Clip(pixelY, 0, mapSize - 1) / mapSize);

         latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
         longitude = 360 * x;
        return new MutablePair<>(latitude,longitude);
    }

    /// <summary>  
    /// Converts pixel XY coordinates into tile XY coordinates of the tile containing  
    /// the specified pixel.  
    /// </summary>  
    /// <param name="pixelX">Pixel X coordinate.</param>  
    /// <param name="pixelY">Pixel Y coordinate.</param>  
    /// <param name="tileX">Output parameter receiving the tile X coordinate.</param>  
    /// <param name="tileY">Output parameter receiving the tile Y coordinate.</param>  
    public static MutablePair<Integer, Integer> pixelXYToTileXY(Integer pixelX, Integer pixelY)
    {
        Integer tileX = Integer.valueOf(pixelX / 256);
        Integer tileY = Integer.valueOf(pixelY / 256);
        return new MutablePair<>(tileX, tileY);
    }

    /// <summary>  
    /// Converts tile XY coordinates into pixel XY coordinates of the upper-left pixel  
    /// of the specified tile.  
    /// </summary>  
    /// <param name="tileX">Tile X coordinate.</param>  
    /// <param name="tileY">Tile Y coordinate.</param>  
    /// <param name="pixelX">Output parameter receiving the pixel X coordinate.</param>  
    /// <param name="pixelY">Output parameter receiving the pixel Y coordinate.</param>  
    public static MutablePair<Integer, Integer> TileXYToPixelXY(Integer tileX, Integer tileY, Integer pixelX, Integer pixelY)
    {
        pixelX = tileX * 256;
        pixelY = tileY * 256;
        return new MutablePair<>(pixelX, pixelY);
    }

    /// <summary>  
    /// Converts tile XY coordinates into a QuadKey at a specified level of detail.  
    /// </summary>  
    /// <param name="tileX">Tile X coordinate.</param>  
    /// <param name="tileY">Tile Y coordinate.</param>  
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)  
    /// to 23 (highest detail).</param>  
    /// <returns>A String containing the QuadKey.</returns>  
    public static String tileXYToQuadKey(Integer tileX, Integer tileY, Integer levelOfDetail)
    {
        StringBuilder quadKey = new StringBuilder();
        for (Integer i = levelOfDetail; i > 0; i--)
        {
            char digit = '0';
            Integer mask = 1 << (i - 1);
            if ((tileX & mask) != 0)
            {
                digit++;
            }
            if ((tileY & mask) != 0)
            {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }

    /// <summary>  
    /// Converts a QuadKey into tile XY coordinates.  
    /// </summary>  
    /// <param name="quadKey">QuadKey of the tile.</param>  
    /// <param name="tileX">Output parameter receiving the tile X coordinate.</param>  
    /// <param name="tileY">Output parameter receiving the tile Y coordinate.</param>  
    /// <param name="levelOfDetail">Output parameter receiving the level of detail.</param>  
    public static MutablePair<Integer, Integer> quadKeyToTileXY(String quadKey, Integer tileX, Integer tileY, Integer levelOfDetail) throws Exception {
        tileX = tileY = 0;
        levelOfDetail = quadKey.length();
        for (Integer i = levelOfDetail; i > 0; i--)
        {
            Integer mask = 1 << (i - 1);
            switch (quadKey.charAt(levelOfDetail - i))
            {
                case '0':
                    break;

                case '1':
                    tileX |= mask;
                    break;

                case '2':
                    tileY |= mask;
                    break;

                case '3':
                    tileX |= mask;
                    tileY |= mask;
                    break;

                default:
                    throw new Exception("Invalid QuadKey digit sequence.");
            }
        }
        return new MutablePair<>(tileX, tileY);
    }
}  
