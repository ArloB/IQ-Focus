package arlob.iqfocus;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static arlob.iqfocus.Solution.SOLUTIONS;
import static arlob.iqfocus.TestUtility.BAD_PIECES;
import static org.junit.Assert.assertTrue;


public class PlacementStringWellFormedTest {
    @Rule
    public Timeout globalTimeout = Timeout.millis(50);

    private void test(String in, boolean expected) {
        boolean out = FocusGame.isPlacementStringWellFormed(in);
        assertTrue("Input was '" + in + "', expected " + expected + " but got " + out, out == expected);
    }

    @Test
    public void n4Pieces() {
        test("", false);
        test(SOLUTIONS[100].placement.substring(0, 8), true);
        test(SOLUTIONS[101].placement.substring(4, 24) + "a", false);
        test(SOLUTIONS[102].placement, true);
    }

    @Test
    public void piecesWellFormed() {
        for (int i = 0; i < SOLUTIONS.length; i++)
            for (int j = 4; j < SOLUTIONS[i].placement.length(); j += 4) {
                test(SOLUTIONS[i].placement.substring(0, j), true);
                test(SOLUTIONS[i].placement.substring(0, j - 4) + BAD_PIECES[i % BAD_PIECES.length], false);
            }
    }

    @Test
    public void duplicatesA() {
        for (int i = 0; i < 40; i += 4) {
            if (i == 16)
                continue;
            String bad = SOLUTIONS[100].placement.substring(0, i) +
                    SOLUTIONS[100].placement.substring(16, 20) +
                    SOLUTIONS[100].placement.substring(i + 4, 40);
            test(SOLUTIONS[i + 32].placement, true);
            test(bad, false);
        }
    }

    @Test
    public void duplicatesB() {
        for (int i = 0; i < 40; i += 4) {
            if (i == 16)
                continue;
            String bad = SOLUTIONS[100].placement.substring(0, i) +
                    SOLUTIONS[100].placement.substring(16, 20).charAt(0) +
                    "003" +
                    SOLUTIONS[100].placement.substring(i + 4, 40);
            test(SOLUTIONS[i + 32].placement, true);
            test(bad, false);
        }
    }
}
