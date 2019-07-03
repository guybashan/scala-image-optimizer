package com.metatools.image

import org.apache.commons.io.IOUtils
import org.scalatest.{FunSpec, _}

import scala.util.Try

class ImageOptimizerSpec extends FunSpec with Matchers {

  private val notOptimizedJpeg = IOUtils.toByteArray(getClass.getResourceAsStream("/media-samples/sample_image_not_optimized.jpg"))
  private val expectedOptimizedJpeg = IOUtils.toByteArray(getClass.getResourceAsStream("/media-samples/sample_image_optimized.jpg"))

  describe("ImageOptimizerSpec") {

    it("Should optimize Jpeg with maximum visual diff of: 0.75") {

      val jpegOptimizer: JpegOptimizer = new JpegOptimizer()
      val optimizedJpeg: Try[Array[Byte]] = jpegOptimizer.optimize(notOptimizedJpeg, 0.75)

      optimizedJpeg.get shouldBe expectedOptimizedJpeg

    }

  }

}
