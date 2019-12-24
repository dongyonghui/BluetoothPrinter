/*
 * Copyright 2008 ZXing authors
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

package cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon;

import cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGF;
import cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Implements Reed-Solomon enbcoding, as the name implies.</p>
 *
 * @author Sean Owen
 * @author William Rucklidge
 */
public final class ReedSolomonEncoder {

  private final cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGF field;
  private final List<cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly> cachedGenerators;

  public ReedSolomonEncoder(GenericGF field) {
    this.field = field;
    this.cachedGenerators = new ArrayList<>();
    cachedGenerators.add(new cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly(field, new int[]{1}));
  }

  private cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly buildGenerator(int degree) {
    if (degree >= cachedGenerators.size()) {
      cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly lastGenerator = cachedGenerators.get(cachedGenerators.size() - 1);
      for (int d = cachedGenerators.size(); d <= degree; d++) {
        cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly nextGenerator = lastGenerator.multiply(
            new cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly(field, new int[] { 1, field.exp(d - 1 + field.getGeneratorBase()) }));
        cachedGenerators.add(nextGenerator);
        lastGenerator = nextGenerator;
      }
    }
    return cachedGenerators.get(degree);
  }

  public void encode(int[] toEncode, int ecBytes) {
    if (ecBytes == 0) {
      throw new IllegalArgumentException("No error correction bytes");
    }
    int dataBytes = toEncode.length - ecBytes;
    if (dataBytes <= 0) {
      throw new IllegalArgumentException("No data bytes provided");
    }
    cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly generator = buildGenerator(ecBytes);
    int[] infoCoefficients = new int[dataBytes];
    System.arraycopy(toEncode, 0, infoCoefficients, 0, dataBytes);
    cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly info = new cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly(field, infoCoefficients);
    info = info.multiplyByMonomial(ecBytes, 1);
    cn.vip.dw.bluetoothprinterlib.zxing.common.reedsolomon.GenericGFPoly remainder = info.divide(generator)[1];
    int[] coefficients = remainder.getCoefficients();
    int numZeroCoefficients = ecBytes - coefficients.length;
    for (int i = 0; i < numZeroCoefficients; i++) {
      toEncode[dataBytes + i] = 0;
    }
    System.arraycopy(coefficients, 0, toEncode, dataBytes + numZeroCoefficients, coefficients.length);
  }

}
