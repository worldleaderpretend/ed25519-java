package net.i2p.crypto.eddsa;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author str4d
 *
 */
public class EdDSAEngineTest {
    static final byte[] ZERO_SEED = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000");
    static final byte[] ZERO_PK = Utils.hexToBytes("3b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29");
    static final byte[] ZERO_MSG_SIG = Utils.hexToBytes("94825896c7075c31bcb81f06dba2bdcd9dcf16e79288d4b9f87c248215c8468d475f429f3de3b4a2cf67fe17077ae19686020364d6d4fa7a0174bab4a123ba0f");

    static final EdDSAParameterSpec ed25519Spec = new EdDSAParameterSpec(
            Constants.b,
            "SHA-512",
            Constants.q,
            null,
            Constants.d,
            Constants.l,
            Constants.B);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testSign() throws Exception {
        //Signature sgr = Signature.getInstance("EdDSA", "I2P");
        Signature sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));

        EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(ZERO_SEED, ed25519Spec);
        PrivateKey sKey = new EdDSAPrivateKey(privKey);
        sgr.initSign(sKey);

        byte[] message = "This is a secret message".getBytes(Charset.forName("UTF-8"));
        sgr.update(message);

        byte[] sig = sgr.sign();
        assertThat(sig, is(equalTo(ZERO_MSG_SIG)));
    }

    @Test
    public void testVerify() throws Exception {
        //Signature sgr = Signature.getInstance("EdDSA", "I2P");
        Signature sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));

        EdDSAPublicKeySpec pubKey = new EdDSAPublicKeySpec(ZERO_PK, ed25519Spec);
        PublicKey vKey = new EdDSAPublicKey(pubKey);
        sgr.initVerify(vKey);

        byte[] message = "This is a secret message".getBytes(Charset.forName("UTF-8"));
        sgr.update(message);

        assertThat(sgr.verify(ZERO_MSG_SIG), is(true));
    }

    /**
     * Checks that a wrong-length signature throws an IAE.
     */
    @Test
    public void testVerifyWrongSigLength() throws Exception {
        //Signature sgr = Signature.getInstance("EdDSA", "I2P");
        Signature sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));

        EdDSAPublicKeySpec pubKey = new EdDSAPublicKeySpec(ZERO_PK, ed25519Spec);
        PublicKey vKey = new EdDSAPublicKey(pubKey);
        sgr.initVerify(vKey);

        byte[] message = "This is a secret message".getBytes(Charset.forName("UTF-8"));
        sgr.update(message);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("signature length is wrong");
        sgr.verify(new byte[] {0});
    }
}
