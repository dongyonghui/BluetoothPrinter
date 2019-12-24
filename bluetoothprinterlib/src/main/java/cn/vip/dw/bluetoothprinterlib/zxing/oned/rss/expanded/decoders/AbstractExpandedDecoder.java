/*
 * Copyright (C) 2010 ZXing authors
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

/*
 * These authors would like to acknowledge the Spanish Ministry of Industry,
 * Tourism and Trade, for the support in the project TSI020301-2008-2
 * "PIRAmIDE: Personalizable Interactions with Resources on AmI-enabled
 * Mobile Dynamic Environments", led by Treelogic
 * ( http://www.treelogic.com/ ):
 *
 *   http://www.piramidepse.com/
 */

package cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders;

import cn.vip.dw.bluetoothprinterlib.zxing.FormatException;
import cn.vip.dw.bluetoothprinterlib.zxing.NotFoundException;
import cn.vip.dw.bluetoothprinterlib.zxing.common.BitArray;
import cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013103decoder;
import cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI01320xDecoder;
import cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI01392xDecoder;
import cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI01393xDecoder;
import cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013x0x1xDecoder;
import cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI01AndOtherAIs;
import cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AnyAIDecoder;
import cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.GeneralAppIdDecoder;

/**
 * @author Pablo Orduña, University of Deusto (pablo.orduna@deusto.es)
 * @author Eduardo Castillejo, University of Deusto (eduardo.castillejo@deusto.es)
 */
public abstract class AbstractExpandedDecoder {

  private final BitArray information;
  private final cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.GeneralAppIdDecoder generalDecoder;

  AbstractExpandedDecoder(BitArray information){
    this.information = information;
    this.generalDecoder = new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.GeneralAppIdDecoder(information);
  }

  protected final BitArray getInformation() {
    return information;
  }

  protected final cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.GeneralAppIdDecoder getGeneralDecoder() {
    return generalDecoder;
  }

  public abstract String parseInformation() throws NotFoundException, FormatException;

  public static AbstractExpandedDecoder createDecoder(BitArray information){
    if (information.get(1)) {
      return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI01AndOtherAIs(information);
    }
    if (!information.get(2)) {
      return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AnyAIDecoder(information);
    }

    int fourBitEncodationMethod = cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.GeneralAppIdDecoder.extractNumericValueFromBitArray(information, 1, 4);

    switch(fourBitEncodationMethod){
      case 4: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013103decoder(information);
      case 5: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI01320xDecoder(information);
    }

    int fiveBitEncodationMethod = cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.GeneralAppIdDecoder.extractNumericValueFromBitArray(information, 1, 5);
    switch(fiveBitEncodationMethod){
      case 12: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI01392xDecoder(information);
      case 13: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI01393xDecoder(information);
    }

    int sevenBitEncodationMethod = cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.GeneralAppIdDecoder.extractNumericValueFromBitArray(information, 1, 7);
    switch(sevenBitEncodationMethod){
      case 56: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013x0x1xDecoder(information, "310", "11");
      case 57: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013x0x1xDecoder(information, "320", "11");
      case 58: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013x0x1xDecoder(information, "310", "13");
      case 59: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013x0x1xDecoder(information, "320", "13");
      case 60: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013x0x1xDecoder(information, "310", "15");
      case 61: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013x0x1xDecoder(information, "320", "15");
      case 62: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013x0x1xDecoder(information, "310", "17");
      case 63: return new cn.vip.dw.bluetoothprinterlib.zxing.oned.rss.expanded.decoders.AI013x0x1xDecoder(information, "320", "17");
    }

    throw new IllegalStateException("unknown decoder: " + information);
  }

}
