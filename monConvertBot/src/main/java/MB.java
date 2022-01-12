import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class MB {
    @SerializedName("Ccy")
    private String mCcy;
    @SerializedName("CcyNm_EN")
    private String mCcyNmEN;
    @SerializedName("CcyNm_RU")
    private String mCcyNmRU;
    @SerializedName("CcyNm_UZ")
    private String mCcyNmUZ;
    @SerializedName("CcyNm_UZC")
    private String mCcyNmUZC;
    @SerializedName("Code")
    private String mCode;
    @SerializedName("Date")
    private String mDate;
    @SerializedName("Diff")
    private String mDiff;
    @SerializedName("id")
    private Long mId;
    @SerializedName("Nominal")
    private String mNominal;
    @SerializedName("Rate")
    private String mRate;
}
