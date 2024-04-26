/*
 * Copyright 2022 NXP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jetpackexample;

import com.jetpackexample.utils.Utils;

import java.io.Serializable;

public class UwbDeviceConfigData implements Serializable {
    public short specVerMajor;
    public short specVerMinor;
    public byte preferredUpdateRate;
    public byte[] rfu;
    public byte deviceRangingRole;
    public byte[] deviceMacAddress;
//    public byte uwbConfigDataLen; // sizeof(uwbConfigData)

    // uwbConfigData:

    //  0: public short major
    //  2: public short minor
    //  4: public byte 3F
    //  5: public byte F5
    //  6: public byte 03
    //  7: public byte 00
    //  8: public byte B8
    //  9: public byte 0B
    // 10: public byte 00
    // 11: public byte 00
    // 12: public byte 00
    // 13: public byte 00
    // 14: public byte 01
    // 15: public byte 01
    // 16: rangingRole 01
    // 17: public byte[] deviceMacAddress
    // 19: public byte 19
    // 20: public byte 00

    public UwbDeviceConfigData() {
        specVerMajor = 1;
        specVerMinor = 0;
        preferredUpdateRate = 20;   // 0..automatic, 10..infrequent, 20..user interactive
        rfu = new byte[10];
        deviceRangingRole = 0;      // 0..controlee, 1..controller
    }

    public byte[] toByteArray() {
        byte[] response = null;
        byte[] config = null;
        response = Utils.concat(response, Utils.shortToByteArray(this.specVerMajor));
        response = Utils.concat(response, Utils.shortToByteArray(this.specVerMinor));
        response = Utils.concat(response, Utils.shortToByteArray(this.preferredUpdateRate));
        response = Utils.concat(response, this.rfu);

        config = Utils.concat(config, Utils.shortToByteArray((short)1));
        config = Utils.concat(config, Utils.shortToByteArray((short)1));
        config = Utils.concat(config, new byte[]{0x3F, (byte)0xF5, 3, 0, (byte)0xB8, 11, 0, 0, 0, 0, 1, 1, 1 });
        config = Utils.concat(config, this.deviceMacAddress);
        config = Utils.concat(config, Utils.byteToByteArray(this.deviceRangingRole));
        config = Utils.concat(config, new byte[]{0x19, 0});

        response = Utils.concat(response, Utils.byteToByteArray((byte)config.length));
        response = Utils.concat(response, config);
        return response;
    }

    public static UwbDeviceConfigData fromByteArray(byte[] data) {
        UwbDeviceConfigData uwbDeviceConfigData = new UwbDeviceConfigData();
        uwbDeviceConfigData.specVerMajor = Utils.byteArrayToShort(Utils.extract(data, 2, 0));
        uwbDeviceConfigData.specVerMinor = Utils.byteArrayToShort(Utils.extract(data, 2, 2));
        uwbDeviceConfigData.preferredUpdateRate = Utils.byteArrayToByte(Utils.extract(data, 1, 4));
        uwbDeviceConfigData.rfu = Utils.extract(data, 10, 5);
        byte uwbConfigDataLength = Utils.byteArrayToByte(Utils.extract(data, 1, 15));
        byte[] uwbConfigData = Utils.extract(data, uwbConfigDataLength, 16);
        if (uwbConfigDataLength == 21) {
            uwbDeviceConfigData.deviceRangingRole = Utils.byteArrayToByte(Utils.extract(uwbConfigData, 1, 16));
            uwbDeviceConfigData.deviceMacAddress = Utils.extract(uwbConfigData, 2, 17);
        }

        return uwbDeviceConfigData;
    }
}
