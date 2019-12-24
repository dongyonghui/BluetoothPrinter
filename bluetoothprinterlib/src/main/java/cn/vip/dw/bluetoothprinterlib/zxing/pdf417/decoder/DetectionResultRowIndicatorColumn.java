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

import cn.vip.dw.bluetoothprinterlib.zxing.FormatException;
import cn.vip.dw.bluetoothprinterlib.zxing.ResultPoint;
import cn.vip.dw.bluetoothprinterlib.zxing.pdf417.PDF417Common;
import cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeMetadata;
import cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeValue;
import cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BoundingBox;
import cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword;
import cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.DetectionResultColumn;

/**
 * @author Guenther Grau
 */
final class DetectionResultRowIndicatorColumn extends cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.DetectionResultColumn {

  private final boolean isLeft;

  DetectionResultRowIndicatorColumn(cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BoundingBox boundingBox, boolean isLeft) {
    super(boundingBox);
    this.isLeft = isLeft;
  }

  void setRowNumbers() {
    for (cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword codeword : getCodewords()) {
      if (codeword != null) {
        codeword.setRowNumberAsRowIndicatorColumn();
      }
    }
  }

  // TODO implement properly
  // TODO maybe we should add missing codewords to store the correct row number to make
  // finding row numbers for other columns easier
  // use row height count to make detection of invalid row numbers more reliable
  int adjustCompleteIndicatorColumnRowNumbers(cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeMetadata barcodeMetadata) {
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword[] codewords = getCodewords();
    setRowNumbers();
    removeIncorrectCodewords(codewords, barcodeMetadata);
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BoundingBox boundingBox = getBoundingBox();
    ResultPoint top = isLeft ? boundingBox.getTopLeft() : boundingBox.getTopRight();
    ResultPoint bottom = isLeft ? boundingBox.getBottomLeft() : boundingBox.getBottomRight();
    int firstRow = imageRowToCodewordIndex((int) top.getY());
    int lastRow = imageRowToCodewordIndex((int) bottom.getY());
    // We need to be careful using the average row height. Barcode could be skewed so that we have smaller and 
    // taller rows
    float averageRowHeight = (lastRow - firstRow) / (float) barcodeMetadata.getRowCount();
    int barcodeRow = -1;
    int maxRowHeight = 1;
    int currentRowHeight = 0;
    for (int codewordsRow = firstRow; codewordsRow < lastRow; codewordsRow++) {
      if (codewords[codewordsRow] == null) {
        continue;
      }
      cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword codeword = codewords[codewordsRow];

      //      float expectedRowNumber = (codewordsRow - firstRow) / averageRowHeight;
      //      if (Math.abs(codeword.getRowNumber() - expectedRowNumber) > 2) {
      //        SimpleLog.log(LEVEL.WARNING,
      //            "Removing codeword, rowNumberSkew too high, codeword[" + codewordsRow + "]: Expected Row: " +
      //                expectedRowNumber + ", RealRow: " + codeword.getRowNumber() + ", value: " + codeword.getValue());
      //        codewords[codewordsRow] = null;
      //      }

      int rowDifference = codeword.getRowNumber() - barcodeRow;

      // TODO improve handling with case where first row indicator doesn't start with 0

      if (rowDifference == 0) {
        currentRowHeight++;
      } else if (rowDifference == 1) {
        maxRowHeight = Math.max(maxRowHeight, currentRowHeight);
        currentRowHeight = 1;
        barcodeRow = codeword.getRowNumber();
      } else if (rowDifference < 0 ||
                 codeword.getRowNumber() >= barcodeMetadata.getRowCount() ||
                 rowDifference > codewordsRow) {
        codewords[codewordsRow] = null;
      } else {
        int checkedRows;
        if (maxRowHeight > 2) {
          checkedRows = (maxRowHeight - 2) * rowDifference;
        } else {
          checkedRows = rowDifference;
        }
        boolean closePreviousCodewordFound = checkedRows >= codewordsRow;
        for (int i = 1; i <= checkedRows && !closePreviousCodewordFound; i++) {
          // there must be (height * rowDifference) number of codewords missing. For now we assume height = 1.
          // This should hopefully get rid of most problems already.
          closePreviousCodewordFound = codewords[codewordsRow - i] != null;
        }
        if (closePreviousCodewordFound) {
          codewords[codewordsRow] = null;
        } else {
          barcodeRow = codeword.getRowNumber();
          currentRowHeight = 1;
        }
      }
    }
    return (int) (averageRowHeight + 0.5);
  }

  int[] getRowHeights() throws FormatException {
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeMetadata barcodeMetadata = getBarcodeMetadata();
    if (barcodeMetadata == null) {
      return null;
    }
    adjustIncompleteIndicatorColumnRowNumbers(barcodeMetadata);
    int[] result = new int[barcodeMetadata.getRowCount()];
    for (cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword codeword : getCodewords()) {
      if (codeword != null) {
        int rowNumber = codeword.getRowNumber();
        if (rowNumber >= result.length) {
          throw FormatException.getFormatInstance();
        }
        result[rowNumber]++;
      } // else throw exception?
    }
    return result;
  }

  // TODO maybe we should add missing codewords to store the correct row number to make
  // finding row numbers for other columns easier
  // use row height count to make detection of invalid row numbers more reliable
  int adjustIncompleteIndicatorColumnRowNumbers(cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeMetadata barcodeMetadata) {
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BoundingBox boundingBox = getBoundingBox();
    ResultPoint top = isLeft ? boundingBox.getTopLeft() : boundingBox.getTopRight();
    ResultPoint bottom = isLeft ? boundingBox.getBottomLeft() : boundingBox.getBottomRight();
    int firstRow = imageRowToCodewordIndex((int) top.getY());
    int lastRow = imageRowToCodewordIndex((int) bottom.getY());
    float averageRowHeight = (lastRow - firstRow) / (float) barcodeMetadata.getRowCount();
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword[] codewords = getCodewords();
    int barcodeRow = -1;
    int maxRowHeight = 1;
    int currentRowHeight = 0;
    for (int codewordsRow = firstRow; codewordsRow < lastRow; codewordsRow++) {
      if (codewords[codewordsRow] == null) {
        continue;
      }
      cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword codeword = codewords[codewordsRow];

      codeword.setRowNumberAsRowIndicatorColumn();

      int rowDifference = codeword.getRowNumber() - barcodeRow;

      // TODO improve handling with case where first row indicator doesn't start with 0

      if (rowDifference == 0) {
        currentRowHeight++;
      } else if (rowDifference == 1) {
        maxRowHeight = Math.max(maxRowHeight, currentRowHeight);
        currentRowHeight = 1;
        barcodeRow = codeword.getRowNumber();
      } else if (codeword.getRowNumber() >= barcodeMetadata.getRowCount()) {
        codewords[codewordsRow] = null;
      } else {
        barcodeRow = codeword.getRowNumber();
        currentRowHeight = 1;
      }
    }
    return (int) (averageRowHeight + 0.5);
  }

  cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeMetadata getBarcodeMetadata() {
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword[] codewords = getCodewords();
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeValue barcodeColumnCount = new cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeValue();
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeValue barcodeRowCountUpperPart = new cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeValue();
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeValue barcodeRowCountLowerPart = new cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeValue();
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeValue barcodeECLevel = new cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeValue();
    for (cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword codeword : codewords) {
      if (codeword == null) {
        continue;
      }
      codeword.setRowNumberAsRowIndicatorColumn();
      int rowIndicatorValue = codeword.getValue() % 30;
      int codewordRowNumber = codeword.getRowNumber();
      if (!isLeft) {
        codewordRowNumber += 2;
      }
      switch (codewordRowNumber % 3) {
        case 0:
          barcodeRowCountUpperPart.setValue(rowIndicatorValue * 3 + 1);
          break;
        case 1:
          barcodeECLevel.setValue(rowIndicatorValue / 3);
          barcodeRowCountLowerPart.setValue(rowIndicatorValue % 3);
          break;
        case 2:
          barcodeColumnCount.setValue(rowIndicatorValue + 1);
          break;
      }
    }
    // Maybe we should check if we have ambiguous values?
    if ((barcodeColumnCount.getValue().length == 0) ||
        (barcodeRowCountUpperPart.getValue().length == 0) ||
        (barcodeRowCountLowerPart.getValue().length == 0) ||
        (barcodeECLevel.getValue().length == 0) ||
        barcodeColumnCount.getValue()[0] < 1 ||
        barcodeRowCountUpperPart.getValue()[0] + barcodeRowCountLowerPart.getValue()[0] < PDF417Common.MIN_ROWS_IN_BARCODE ||
        barcodeRowCountUpperPart.getValue()[0] + barcodeRowCountLowerPart.getValue()[0] > PDF417Common.MAX_ROWS_IN_BARCODE) {
      return null;
    }
    cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeMetadata barcodeMetadata = new cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeMetadata(barcodeColumnCount.getValue()[0],
        barcodeRowCountUpperPart.getValue()[0], barcodeRowCountLowerPart.getValue()[0], barcodeECLevel.getValue()[0]);
    removeIncorrectCodewords(codewords, barcodeMetadata);
    return barcodeMetadata;
  }

  private void removeIncorrectCodewords(cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword[] codewords, cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.BarcodeMetadata barcodeMetadata) {
    // Remove codewords which do not match the metadata
    // TODO Maybe we should keep the incorrect codewords for the start and end positions?
    for (int codewordRow = 0; codewordRow < codewords.length; codewordRow++) {
      cn.vip.dw.bluetoothprinterlib.zxing.pdf417.decoder.Codeword codeword = codewords[codewordRow];
      if (codewords[codewordRow] == null) {
        continue;
      }
      int rowIndicatorValue = codeword.getValue() % 30;
      int codewordRowNumber = codeword.getRowNumber();
      if (codewordRowNumber > barcodeMetadata.getRowCount()) {
        codewords[codewordRow] = null;
        continue;
      }
      if (!isLeft) {
        codewordRowNumber += 2;
      }
      switch (codewordRowNumber % 3) {
        case 0:
          if (rowIndicatorValue * 3 + 1 != barcodeMetadata.getRowCountUpperPart()) {
            codewords[codewordRow] = null;
          }
          break;
        case 1:
          if (rowIndicatorValue / 3 != barcodeMetadata.getErrorCorrectionLevel() ||
              rowIndicatorValue % 3 != barcodeMetadata.getRowCountLowerPart()) {
            codewords[codewordRow] = null;
          }
          break;
        case 2:
          if (rowIndicatorValue + 1 != barcodeMetadata.getColumnCount()) {
            codewords[codewordRow] = null;
          }
          break;
      }
    }
  }

  boolean isLeft() {
    return isLeft;
  }

  @Override
  public String toString() {
    return "IsLeft: " + isLeft + '\n' + super.toString();
  }

}
