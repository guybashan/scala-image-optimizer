package com.metatools.image

import scala.annotation.tailrec
import scala.util.Try

trait ImageOptimizer {

  def optimize(jpeg: Array[Byte], maxVisualDiff: Double): Try[Array[Byte]]

}

class JpegOptimizer extends ImageOptimizer {

  def optimize(jpeg: Array[Byte], maxVisualDiff: Double): Try[Array[Byte]] = {

    @tailrec
    def binarySearch(low: Int, high: Int): Int = {
      val midQuality: Int = (low + high) / 2
      val similarityLevel = ImageUtils.rgbSimilarityLevel(jpeg, midQuality) * 100

      if (low == high) {
        midQuality
      } else if (similarityLevel < maxVisualDiff) {
        binarySearch(low, midQuality - 1)
      } else {
        binarySearch(midQuality + 1, high)
      }
    }

    Try {
      val mid: Int = binarySearch(0, 100)
      ImageUtils.createJpegWithQualityFromJpeg(jpeg, mid)
    }
  }

}
