/*
 * Copyright 2007 ZXing authors
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

package cn.vip.dw.bluetoothprinterlib.zxing;

import cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat;
import cn.vip.dw.bluetoothprinterlib.zxing.BinaryBitmap;
import cn.vip.dw.bluetoothprinterlib.zxing.DecodeHintType;
import cn.vip.dw.bluetoothprinterlib.zxing.NotFoundException;
import cn.vip.dw.bluetoothprinterlib.zxing.Reader;
import cn.vip.dw.bluetoothprinterlib.zxing.ReaderException;
import cn.vip.dw.bluetoothprinterlib.zxing.Result;
import cn.vip.dw.bluetoothprinterlib.zxing.aztec.AztecReader;
import cn.vip.dw.bluetoothprinterlib.zxing.datamatrix.DataMatrixReader;
import cn.vip.dw.bluetoothprinterlib.zxing.maxicode.MaxiCodeReader;
import cn.vip.dw.bluetoothprinterlib.zxing.oned.MultiFormatOneDReader;
import cn.vip.dw.bluetoothprinterlib.zxing.pdf417.PDF417Reader;
import cn.vip.dw.bluetoothprinterlib.zxing.qrcode.QRCodeReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * MultiFormatReader is a convenience class and the main entry point into the library for most uses.
 * By default it attempts to decode all barcode formats that the library supports. Optionally, you
 * can provide a hints object to request different behavior, for example only decoding QR codes.
 *
 * @author Sean Owen
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class MultiFormatReader implements Reader {

  private Map<cn.vip.dw.bluetoothprinterlib.zxing.DecodeHintType,?> hints;
  private Reader[] readers;

  /**
   * This version of decode honors the intent of Reader.decode(BinaryBitmap) in that it
   * passes null as a hint to the decoders. However, that makes it inefficient to call repeatedly.
   * Use setHints() followed by decodeWithState() for continuous scan applications.
   *
   * @param image The pixel data to decode
   * @return The contents of the image
   * @throws NotFoundException Any errors which occurred
   */
  @Override
  public Result decode(cn.vip.dw.bluetoothprinterlib.zxing.BinaryBitmap image) throws NotFoundException {
    setHints(null);
    return decodeInternal(image);
  }

  /**
   * Decode an image using the hints provided. Does not honor existing state.
   *
   * @param image The pixel data to decode
   * @param hints The hints to use, clearing the previous state.
   * @return The contents of the image
   * @throws NotFoundException Any errors which occurred
   */
  @Override
  public Result decode(cn.vip.dw.bluetoothprinterlib.zxing.BinaryBitmap image, Map<cn.vip.dw.bluetoothprinterlib.zxing.DecodeHintType,?> hints) throws NotFoundException {
    setHints(hints);
    return decodeInternal(image);
  }

  /**
   * Decode an image using the state set up by calling setHints() previously. Continuous scan
   * clients will get a <b>large</b> speed increase by using this instead of decode().
   *
   * @param image The pixel data to decode
   * @return The contents of the image
   * @throws NotFoundException Any errors which occurred
   */
  public Result decodeWithState(cn.vip.dw.bluetoothprinterlib.zxing.BinaryBitmap image) throws NotFoundException {
    // Make sure to set up the default state so we don't crash
    if (readers == null) {
      setHints(null);
    }
    return decodeInternal(image);
  }

  /**
   * This method adds state to the MultiFormatReader. By setting the hints once, subsequent calls
   * to decodeWithState(image) can reuse the same set of readers without reallocating memory. This
   * is important for performance in continuous scan clients.
   *
   * @param hints The set of hints to use for subsequent calls to decode(image)
   */
  public void setHints(Map<cn.vip.dw.bluetoothprinterlib.zxing.DecodeHintType,?> hints) {
    this.hints = hints;

    boolean tryHarder = hints != null && hints.containsKey(cn.vip.dw.bluetoothprinterlib.zxing.DecodeHintType.TRY_HARDER);
    @SuppressWarnings("unchecked")
    Collection<cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat> formats =
        hints == null ? null : (Collection<cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat>) hints.get(DecodeHintType.POSSIBLE_FORMATS);
    Collection<Reader> readers = new ArrayList<>();
    if (formats != null) {
      boolean addOneDReader =
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.UPC_A) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.UPC_E) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.EAN_13) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.EAN_8) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.CODABAR) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.CODE_39) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.CODE_93) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.CODE_128) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.ITF) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.RSS_14) ||
          formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.RSS_EXPANDED);
      // Put 1D readers upfront in "normal" mode
      if (addOneDReader && !tryHarder) {
        readers.add(new MultiFormatOneDReader(hints));
      }
      if (formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.QR_CODE)) {
        readers.add(new QRCodeReader());
      }
      if (formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.DATA_MATRIX)) {
        readers.add(new DataMatrixReader());
      }
      if (formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.AZTEC)) {
        readers.add(new AztecReader());
      }
      if (formats.contains(cn.vip.dw.bluetoothprinterlib.zxing.BarcodeFormat.PDF_417)) {
         readers.add(new PDF417Reader());
      }
      if (formats.contains(BarcodeFormat.MAXICODE)) {
         readers.add(new MaxiCodeReader());
      }
      // At end in "try harder" mode
      if (addOneDReader && tryHarder) {
        readers.add(new MultiFormatOneDReader(hints));
      }
    }
    if (readers.isEmpty()) {
      if (!tryHarder) {
        readers.add(new MultiFormatOneDReader(hints));
      }

      readers.add(new QRCodeReader());
      readers.add(new DataMatrixReader());
      readers.add(new AztecReader());
      readers.add(new PDF417Reader());
      readers.add(new MaxiCodeReader());

      if (tryHarder) {
        readers.add(new MultiFormatOneDReader(hints));
      }
    }
    this.readers = readers.toArray(new Reader[readers.size()]);
  }

  @Override
  public void reset() {
    if (readers != null) {
      for (Reader reader : readers) {
        reader.reset();
      }
    }
  }

  private Result decodeInternal(BinaryBitmap image) throws NotFoundException {
    if (readers != null) {
      for (Reader reader : readers) {
        try {
          return reader.decode(image, hints);
        } catch (ReaderException re) {
          // continue
        }
      }
    }
    throw NotFoundException.getNotFoundInstance();
  }

}
