package com.metatools.image

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import javax.imageio._
import javax.imageio.stream.ImageOutputStream


object ImageUtils {

  def toBufferedImage(jpeg: Array[Byte]): BufferedImage = {
    ImageIO.read(new ByteArrayInputStream(jpeg))
  }

  def createJpegWithQualityFromJpeg(jpeg: Array[Byte], quality: Int): Array[Byte] = {
      val os = new ByteArrayOutputStream()
      val imageWriter: ImageWriter = ImageIO.getImageWritersByFormatName("jpg").next

      try {
        val jpegWithNewQuality: ImageOutputStream = ImageIO.createImageOutputStream(os)
        imageWriter.setOutput(jpegWithNewQuality)

        val imageWriteParam = imageWriter.getDefaultWriteParam
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
        imageWriteParam.setCompressionQuality(quality / 100f)

        val sourceBufferedImage: BufferedImage = toBufferedImage(jpeg)
        imageWriter.write(null, new IIOImage(sourceBufferedImage, null, null), imageWriteParam)
      } finally {
        os.close()
        imageWriter.dispose()
      }

      os.toByteArray
  }

  def rgbSimilarityLevel(image1: BufferedImage, image2: BufferedImage): Double = {
    val width1 = image1.getWidth(null)
    val height1 = image1.getHeight(null)

    val width2 = image2.getWidth(null)
    val height2 = image2.getHeight(null)

    require((width1 == width2) && (height1 == height2), "Image sizes must be identical")

    val dataBuffer1 = image1.getRaster.getDataBuffer
    val dataBuffer2 = image2.getRaster.getDataBuffer

    val size = dataBuffer1.getSize

    require(size == width1 * height1 * 3, "Jpeg must have 24bit per pixel. 8 bits per each: R, G, B")

    val diff = (0 until size by 3).foldLeft(0.0) { case (sum, i) =>
      val deltaR: Double = dataBuffer2.getElem(i) - dataBuffer1.getElem(i)
      val deltaG: Double = dataBuffer2.getElem(i + 1) - dataBuffer1.getElem(i + 1)
      val deltaB: Double = dataBuffer2.getElem(i + 2) - dataBuffer1.getElem(i + 2)
      sum + Math.sqrt((deltaR * deltaR + deltaG * deltaG + deltaB * deltaB) / 65025.0)
    }

    val maxPixelDiff = Math.sqrt(3)
    val numberOfPixels = width1 * height1
    diff / (numberOfPixels * maxPixelDiff)
  }

  def rgbSimilarityLevel(jpeg: Array[Byte], quality: Int): Double = {
    val image1: BufferedImage = ImageUtils.toBufferedImage(jpeg)
    val newJpeg = ImageUtils.createJpegWithQualityFromJpeg(jpeg = jpeg, quality = quality)
    val image2: BufferedImage = ImageIO.read(new ByteArrayInputStream(newJpeg))

    ImageUtils.rgbSimilarityLevel(image1, image2)
  }

}
