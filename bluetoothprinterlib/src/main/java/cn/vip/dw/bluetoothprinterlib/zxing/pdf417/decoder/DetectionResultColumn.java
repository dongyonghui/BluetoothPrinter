/*
 * Copyright 2013 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder;

import cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BoundingBox;
import cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword;

import java.util.Formatter;

/**
 * @author Guenther Grau
 */
class DetectionResultColumn {
  
  private static final int MAX_NEARBY_DISTANCE = 5;
  
  private final cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BoundingBox boundingBox;
  private final cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword[] codewords;

  DetectionResultColumn(cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BoundingBox boundingBox) {
    this.boundingBox = new cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BoundingBox(boundingBox);
    codewords = new cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword[boundingBox.getMaxY() - boundingBox.getMinY() + 1];
  }

  final cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword getCodewordNearby(int imageRow) {
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword codeword = getCodeword(imageRow);
    if (codeword != null) {
      return codeword;
    }
    for (int i = 1; i < MAX_NEARBY_DISTANCE; i++) {
      int nearImageRow = imageRowToCodewordIndex(imageRow) - i;
      if (nearImageRow >= 0) {
        codeword = codewords[nearImageRow];
        if (codeword != null) {
          return codeword;
        }
      }
      nearImageRow = imageRowToCodewordIndex(imageRow) + i;
      if (nearImageRow < codewords.length) {
        codeword = codewords[nearImageRow];
        if (codeword != null) {
          return codeword;
        }
      }
    }
    return null;
  }

  final int imageRowToCodewordIndex(int imageRow) {
    return imageRow - boundingBox.getMinY();
  }

  final void setCodeword(int imageRow, cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword codeword) {
    codewords[imageRowToCodewordIndex(imageRow)] = codeword;
  }

  final cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword getCodeword(int imageRow) {
    return codewords[imageRowToCodewordIndex(imageRow)];
  }

  final cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BoundingBox getBoundingBox() {
    return boundingBox;
  }

  final cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword[] getCodewords() {
    return codewords;
  }

  @Override
  public String toString() {
    Formatter formatter = new Formatter();
    int row = 0;
    for (cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword codeword : codewords) {
      if (codeword == null) {
        formatter.format("%3d:    |   %n", row++);
        continue;
      }
      formatter.format("%3d: %3d|%3d%n", row++, codeword.getRowNumber(), codeword.getValue());
    }
    String result = formatter.toString();
    formatter.close();
    return result;
  }

}
