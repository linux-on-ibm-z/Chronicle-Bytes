/*
 * Copyright (c) 2016-2022 chronicle.software
 *
 *     https://chronicle.software
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
package net.openhft.chronicle.bytes;

import org.junit.Test;

import java.text.DecimalFormat;

import static net.openhft.chronicle.bytes.UnsafeTextBytesTest.testAppendDouble;
import static org.junit.Assert.assertEquals;

public class Issue128Test extends BytesTestCommon {
    private static final DecimalFormat DF;

    static {
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(30);
        DF = df;
    }

    static String toDecimal(double d) {
        return DF.format(d);
    }

    @Test
    public void testCorrect() {
        Bytes<?> bytes = Bytes.allocateDirect(32);
        try {
            // odd ones are trouble.
            for (int i = 1; i < 1_000_000; i += 2) {
                double v6 = (double) i / 1_000_000;
                doTest(bytes, v6);
                doTest(bytes, 999 + v6);
                double v7 = (double) i / 10_000_000;
                doTest(bytes, v7);
                double v8 = (double) i / 100_000_000;
                doTest(bytes, v8);
                double v9 = (double) i / 100_000_000;
                doTest(bytes, v9);
            }
        } finally {
            bytes.releaseLast();
        }
    }

    public void doTest(Bytes<?> bytes, double v) {
        String format = DF.format(v);
        String output = testAppendDouble(bytes, v);
        if (Double.parseDouble(output) != v || format.length() != output.length())
            assertEquals(DF.format(v), output);
//            System.out.println(DF.format(v)+" != " + output);
    }
}
