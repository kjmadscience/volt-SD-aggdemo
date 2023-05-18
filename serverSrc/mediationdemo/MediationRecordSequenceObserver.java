package mediationdemo;

import org.voltdb.VoltProcedure.VoltAbortException;

/* This file is part of VoltDB.
 * Copyright (C) 2008-2021 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * This class is used by the Volt server functions getHighestValidSequencand
 * sequenceToString
 *
 */
public class MediationRecordSequenceObserver {

    /**
     * Returns the highest value filled in from zero to 'value', if all values are
     * filled in. So:
     * <ul>
     * <li>If bits 0,1,2 are filled in it returns '2'.</li>
     * <li>If bits 0,1,2,5 are filled in it returns -1.</li>
     * </ul>
     * <p>
     * This is used by the SQL function getHighestValidSequence, which is used by
     * GetBySessionId and MediationRecordSequenceObserver
     * 
     * @param varbinaryArray a byte array containing a java.util.BitSet
     * @return the highest element if and only if all prior elements filled in.
     * @throws VoltAbortException - makes sure any Exceptions won't hurt Volt
     */
    public int getHighestValidSequence(byte[] varbinaryArray) throws VoltAbortException {

        try {
            MediationRecordSequence msr = new MediationRecordSequence(varbinaryArray);

            int seqnoCount = msr.theBitSet.cardinality();

            if (seqnoCount == 0) {
                // No bits are set..
                return -1;
            }

            if (msr.weHaveFromZeroTo(seqnoCount - 1)) {
                // All of the bits up to the highest one are set.
                return seqnoCount - 1;
            }

            return -1;

        } catch (Exception e) {
            throw new VoltAbortException(e);
        }

    }

    /**
     * Takes a byte[] containing a java.util.BitSet and returns a text
     * representation.
     * <p>
     * This is used by the SQL function sequenceToString, which is used by
     * MediationRecordSequenceObserver
     * <p> 
     * <ul>
     * <li>A contiguous block from 0 to 4 would show return '0-4'.</li>
     * <li>0,1 and 4 would return 'XX_X'.
     * </ul>
     * @param varbinaryArray a java.util.BitSet
     * @return A Text representation.
     * @throws VoltAbortException - makes sure any Exceptions won't hurt Volt
     */
    public String getSeqnosAsText(byte[] varbinaryArray) {

        try {
            int highestValidSequence = getHighestValidSequence(varbinaryArray);
            StringBuffer b = new StringBuffer();

            if (highestValidSequence > -1) {

                b.append("0-");
                b.append(highestValidSequence);
                return b.toString();

            }

            MediationRecordSequence msr = new MediationRecordSequence(varbinaryArray);

            return (msr.toString());
            
        } catch (Exception e) {
            throw new VoltAbortException(e);
        }

    }
}
