package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.encoders.Hex;

public class SAKKEPublicKeyParameters extends AsymmetricKeyParameter {
   static final BigInteger p = new BigInteger(
      "997ABB1F0A563FDA65C61198DAD0657A416C0CE19CB48261BE9AE358B3E01A2EF40AAB27E2FC0F1B228730D531A59CB0E791B39FF7C88A19356D27F4A666A6D0E26C6487326B4CD4512AC5CD65681CE1B6AFF4A831852A82A7CF3C521C3C09AA9F94D6AF56971F1FFCE3E82389857DB080C5DF10AC7ACE87666D807AFEA85FEB",
      16
   );
   static final BigInteger q = new BigInteger(
      "265EAEC7C2958FF69971846636B4195E905B0338672D20986FA6B8D62CF8068BBD02AAC9F8BF03C6C8A1CC354C69672C39E46CE7FDF222864D5B49FD2999A9B4389B1921CC9AD335144AB173595A07386DABFD2A0C614AA0A9F3CF14870F026AA7E535ABD5A5C7C7FF38FA08E2615F6C203177C42B1EB3A1D99B601EBFAA17FB",
      16
   );
   private static final BigInteger Px = new BigInteger(
      "53FC09EE332C29AD0A7990053ED9B52A2B1A2FD60AEC69C698B2F204B6FF7CBFB5EDB6C0F6CE2308AB10DB9030B09E1043D5F22CDB9DFA55718BD9E7406CE8909760AF765DD5BCCB337C86548B72F2E1A702C3397A60DE74A7C1514DBA66910DD5CFB4CC80728D87EE9163A5B63F73EC80EC46C4967E0979880DC8ABEAE63895",
      16
   );
   private static final BigInteger Py = new BigInteger(
      "0A8249063F6009F1F9F1F0533634A135D3E82016029906963D778D821E141178F5EA69F4654EC2B9E7F7F5E5F0DE55F66B598CCF9A140B2E416CFF0CA9E032B970DAE117AD547C6CCAD696B5B7652FE0AC6F1E80164AA989492D979FC5A4D5F213515AD7E9CB99A980BDAD5AD5BB4636ADB9B5706A67DCDE75573FD71BEF16D7",
      16
   );
   private static final BigInteger g = new BigInteger(
      1,
      Hex.decode(
         "66FC2A43 2B6EA392 148F1586 7D623068\n               C6A87BD1 FB94C41E 27FABE65 8E015A87\n               371E9474 4C96FEDA 449AE956 3F8BC446\n               CBFDA85D 5D00EF57 7072DA8F 541721BE\n               EE0FAED1 828EAB90 B99DFB01 38C78433\n               55DF0460 B4A9FD74 B4F1A32B CAFA1FFA\n               D682C033 A7942BCC E3720F20 B9B7B040\n               3C8CAE87 B7A0042A CDE0FAB3 6461EA46"
      )
   );
   private static final ECCurve.Fp curve = new ECCurve.Fp(p, p.subtract(BigInteger.valueOf(3L)), BigInteger.ZERO, q, BigInteger.valueOf(4L));
   static final ECPoint P = configureBasepoint(curve, Px, Py);
   private final ECPoint Z;
   private final BigInteger identifier;
   private static final int n = 128;
   private final Digest digest = new SHA256Digest();

   private static ECPoint configureBasepoint(ECCurve var0, BigInteger var1, BigInteger var2) {
      ECPoint var3 = var0.createPoint(var1, var2);
      WNafUtil.configureBasepoint(var3);
      return var3;
   }

   public SAKKEPublicKeyParameters(BigInteger var1, ECPoint var2) {
      super(false);
      this.identifier = var1;
      this.Z = var2;
   }

   public BigInteger getIdentifier() {
      return this.identifier;
   }

   public ECPoint getZ() {
      return this.Z;
   }

   public ECCurve getCurve() {
      return curve;
   }

   public ECPoint getPoint() {
      return P;
   }

   public BigInteger getPrime() {
      return p;
   }

   public BigInteger getQ() {
      return q;
   }

   public int getN() {
      return 128;
   }

   public Digest getDigest() {
      return this.digest;
   }

   public BigInteger getG() {
      return g;
   }
}
