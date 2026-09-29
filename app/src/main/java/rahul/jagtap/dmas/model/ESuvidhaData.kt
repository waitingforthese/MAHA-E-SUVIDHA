package rahul.jagtap.dmas.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ESuvidhaData(
    var map: HashMap<String, HashMap<String, ESuvidhaType>>? = null
) : Parcelable

@Parcelize
data class ESuvidhaType(
    //    @SerializedName("manual_aadhar_pan_card")
    //    var manualAadharPanCards: HashMap<String, ManualAadharPanCard>? = null,
    @SerializedName("pan_cards")
    var panCards: HashMap<String, PanCard>? = null,
    @SerializedName("shop_acts")
    var shopActs: HashMap<String, ShopAct>? = null,
    @SerializedName("udyam_aadhar")
    var udyamAadhars: HashMap<String, UdyamAadhar>? = null,
    @SerializedName("food_license")
    var foodLicenses: HashMap<String, FoodLicense>? = null,
    @SerializedName("provident_fund")
    var providentFunds: HashMap<String, ProvidentFund>? = null,
    @SerializedName("nepal_money_transfer")
    var nepalMoneyTransfers: HashMap<String, NepalMoneyTransfer>? = null,
    @SerializedName("railway_ticket_booking")
    var railwayTicketBookings: HashMap<String, RailwayTicketBooking>? = null,
    @SerializedName("business_pan_cards")
    var businessPanCards: HashMap<String, BusinessPanCard>? = null,
    @SerializedName("election_cards")
    var electionCards: HashMap<String, ElectionCard>? = null,
    @SerializedName("passports")
    var passports: HashMap<String, Passport>? = null,
    @SerializedName("driving_learning_licenses")
    var driving_learning_licenses: HashMap<String, DrivingLearningLicense>? = null,
    @SerializedName("edit_pan_aadhar_cards")
    var edit_pan_aadhar_cards: HashMap<String, EditPanAadharCard>? = null,
    @SerializedName("police_verifications")
    var police_verifications: HashMap<String, PoliceVerification>? = null,
    @SerializedName("income_certificates")
    var income_certificates: HashMap<String, IncomeCertificate>? = null,
    @SerializedName("age_certificates")
    var age_certificates: HashMap<String, AgeCertificate>? = null,
    @SerializedName("gazzets")
    var gazzets: HashMap<String, Gazzet>? = null,
    @SerializedName("jyotish_shastra")
    var jyotish_shastra: HashMap<String, JyotishShastra>? = null,
    @SerializedName("cibil_reports")
    var cibil_reports: HashMap<String, CibilReport>? = null,
    @SerializedName("free_credit_cards")
    var credit_cards: HashMap<String, CreditCard>? = null,
    @SerializedName("gst_regs")
    var gst_regs: HashMap<String, GstRegistration>? = null,
    @SerializedName("all_govt_cards")
    var all_govt_cards: HashMap<String, AllGovtCard>? = null,
    @SerializedName("verification")
    var verification: HashMap<String, Verification>? = null,
    @SerializedName("farmer_policies")
    var farmer_policies: HashMap<String, FarmerPolicy>? = null,
    @SerializedName("all_other_docs")
    var all_other_cards: HashMap<String, AllOtherCard>? = null,
    @SerializedName("demat_accounts")
    var demat_accounts: HashMap<String, DematAccount>? = null,
    @SerializedName("govt_schemes")
    var govt_schemes: HashMap<String, GovtScheme>? = null
) : Parcelable

@Parcelize
data class PanCard(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
//    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("fatherName") @Expose var fatherName: String? = "",
    @SerializedName("dob") @Expose var dob: String? = "",
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String? = "",
//    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("oldPanCardPhotoFileName") @Expose var oldPanCardPhotoFileName: String? = "",
    @SerializedName("oldPanCardPhotoDownloadUrl") @Expose var oldPanCardPhotoDownloadUrl: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("aadharFatherFrontPhotoFileName") @Expose var aadharFatherFrontPhotoFileName: String? = "",
    @SerializedName("aadharFatherFrontPhotoDownloadUrl") @Expose var aadharFatherFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharFatherBackPhotoFileName") @Expose var aadharFatherBackPhotoFileName: String? = "",
    @SerializedName("aadharFatherBackPhotoDownloadUrl") @Expose var aadharFatherBackPhotoDownloadUrl: String? = "",
    @SerializedName("signFileName") @Expose var signFileName: String? = "",
    @SerializedName("signDownloadUrl") @Expose var signDownloadUrl: String? = "",
    @SerializedName("marriageCertFileName") @Expose var marriageCertFileName: String? = "",
    @SerializedName("marriageCertDownloadUrl") @Expose var marriageCertDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("textbox1") @Expose var textbox1: String? = "",
    @SerializedName("textbox1Hint") @Expose var textbox1Hint: String? = "",
    @SerializedName("textbox2") @Expose var textbox2: String? = "",
    @SerializedName("textbox2Hint") @Expose var textbox2Hint: String? = "",
    @SerializedName("textbox3") @Expose var textbox3: String? = "",
    @SerializedName("textbox3Hint") @Expose var textbox3Hint: String? = "",
    @SerializedName("textbox4") @Expose var textbox4: String? = "",
    @SerializedName("textbox4Hint") @Expose var textbox4Hint: String? = "",
    @SerializedName("textbox5") @Expose var textbox5: String? = "",
    @SerializedName("textbox5Hint") @Expose var textbox5Hint: String? = "",
    @SerializedName("datebox") @Expose var datebox: String? = "",
    @SerializedName("dateboxHint") @Expose var dateboxHint: String? = "",
    @SerializedName("attachment1Hint") @Expose var attachment1Hint: String? = "",
    @SerializedName("attachment1FileName") @Expose var attachment1FileName: String? = "",
    @SerializedName("attachment1DownloadUrl") @Expose var attachment1DownloadUrl: String? = "",
    @SerializedName("attachment2Hint") @Expose var attachment2Hint: String? = "",
    @SerializedName("attachment2FileName") @Expose var attachment2FileName: String? = "",
    @SerializedName("attachment2DownloadUrl") @Expose var attachment2DownloadUrl: String? = "",
    @SerializedName("attachment3Hint") @Expose var attachment3Hint: String? = "",
    @SerializedName("attachment3FileName") @Expose var attachment3FileName: String? = "",
    @SerializedName("attachment3DownloadUrl") @Expose var attachment3DownloadUrl: String? = "",
    @SerializedName("attachment4Hint") @Expose var attachment4Hint: String? = "",
    @SerializedName("attachment4FileName") @Expose var attachment4FileName: String? = "",
    @SerializedName("attachment4DownloadUrl") @Expose var attachment4DownloadUrl: String? = "",
    @SerializedName("attachment5Hint") @Expose var attachment5Hint: String? = "",
    @SerializedName("attachment5FileName") @Expose var attachment5FileName: String? = "",
    @SerializedName("attachment5DownloadUrl") @Expose var attachment5DownloadUrl: String? = "",
    @SerializedName("attachment6Hint") @Expose var attachment6Hint: String? = "",
    @SerializedName("attachment6FileName") @Expose var attachment6FileName: String? = "",
    @SerializedName("attachment6DownloadUrl") @Expose var attachment6DownloadUrl: String? = "",
    @SerializedName("attachment7Hint") @Expose var attachment7Hint: String? = "",
    @SerializedName("attachment7FileName") @Expose var attachment7FileName: String? = "",
    @SerializedName("attachment7DownloadUrl") @Expose var attachment7DownloadUrl: String? = "",
    @SerializedName("attachment8Hint") @Expose var attachment8Hint: String? = "",
    @SerializedName("attachment8FileName") @Expose var attachment8FileName: String? = "",
    @SerializedName("attachment8DownloadUrl") @Expose var attachment8DownloadUrl: String? = "",
    @SerializedName("attachment9Hint") @Expose var attachment9Hint: String? = "",
    @SerializedName("attachment9FileName") @Expose var attachment9FileName: String? = "",
    @SerializedName("attachment9DownloadUrl") @Expose var attachment9DownloadUrl: String? = "",
    @SerializedName("attachment10Hint") @Expose var attachment10Hint: String? = "",
    @SerializedName("attachment10FileName") @Expose var attachment10FileName: String? = "",
    @SerializedName("attachment10DownloadUrl") @Expose var attachment10DownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = textbox3 ?: ""
    override val applicantName: String
        get() = textbox1 ?: ""
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class ShopAct(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("businessName") @Expose var businessName: String? = "",
    @SerializedName("businessAddress") @Expose var businessAddress: String? = "",
    @SerializedName("businessStartDate") @Expose var businessStartDate: String? = "",
    @SerializedName("businessInfo") @Expose var businessInfo: String? = "",
//    @SerializedName("ownerName") @Expose var ownerName: String? = "",
    @SerializedName("ownerMobileNo") @Expose var ownerMobileNo: String? = "",
    @SerializedName("ownerEmail") @Expose var ownerEmail: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
    @SerializedName("lightPhotoFileName") @Expose var lightPhotoFileName: String? = "",
    @SerializedName("lightPhotoDownloadUrl") @Expose var lightPhotoDownloadUrl: String? = "",
    @SerializedName("ownerPhotoFileName") @Expose var ownerPhotoFileName: String? = "",
    @SerializedName("ownerPhotoDownloadUrl") @Expose var ownerPhotoDownloadUrl: String? = "",
    @SerializedName("signPhotoFileName") @Expose var signPhotoFileName: String? = "",
    @SerializedName("signPhotoDownloadUrl") @Expose var signPhotoDownloadUrl: String? = "",
    @SerializedName("boardPhotoFileName") @Expose var boardPhotoFileName: String? = "",
    @SerializedName("boardPhotoDownloadUrl") @Expose var boardPhotoDownloadUrl: String? = "",
    @SerializedName("oldShopActPhotoFileName") @Expose var oldShopActPhotoFileName: String? = "",
    @SerializedName("oldShopActPhotoDownloadUrl") @Expose var oldShopActPhotoDownloadUrl: String? = "",
    @SerializedName("partnerPhotoFileName") @Expose var partnerPhotoFileName: String? = "",
    @SerializedName("partnerPhotoDownloadUrl") @Expose var partnerPhotoDownloadUrl: String? = "",
    @SerializedName("partnerSignPhotoFileName") @Expose var partnerSignPhotoFileName: String? = "",
    @SerializedName("partnerSignPhotoDownloadUrl") @Expose var partnerSignPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = ownerMobileNo ?: ""

    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class UdyamAadhar(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("businessName") @Expose var businessName: String? = "",
    @SerializedName("businessAddress") @Expose var businessAddress: String? = "",
    @SerializedName("businessNature") @Expose var businessNature: String? = "",
//    @SerializedName("ownerName") @Expose var ownerName: String? = "",
    @SerializedName("ownerMobileNo") @Expose var ownerMobileNo: String? = "",
    @SerializedName("ownerEmail") @Expose var ownerEmail: String? = "",
    @SerializedName("ownerCast") @Expose var ownerCast: String? = "",
    @SerializedName("businessStartDate") @Expose var businessStartDate: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
    @SerializedName("passbookPhotoFileName") @Expose var passbookPhotoFileName: String? = "",
    @SerializedName("passbookPhotoDownloadUrl") @Expose var passbookPhotoDownloadUrl: String? = "",
    @SerializedName("oldUdyamAadharPhotoFileName") @Expose var oldUdyamAadharPhotoFileName: String? = "",
    @SerializedName("oldUdyamAadharPhotoDownloadUrl") @Expose var oldUdyamAadharPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = ownerMobileNo ?: ""
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class ProvidentFund(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String? = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("personName") @Expose var personName: String? = "",
    @SerializedName("personMobileNo") @Expose var personMobileNo: String? = "",
    @SerializedName("personEmail") @Expose var personEmail: String? = "",
    @SerializedName("uanNumber") @Expose var uanNumber: String? = "",
    @SerializedName("password") @Expose var password: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
    @SerializedName("passbookPhotoFileName") @Expose var passbookPhotoFileName: String? = "",
    @SerializedName("passbookPhotoDownloadUrl") @Expose var passbookPhotoDownloadUrl: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
//    @SerializedName("passportNomineePhotoFileName") @Expose var passportNomineePhotoFileName: String? = "",
//    @SerializedName("passportNomineePhotoDownloadUrl") @Expose var passportNomineePhotoDownloadUrl: String? = "",
//    @SerializedName("aadharNomineePhotoFileName") @Expose var aadharNomineePhotoFileName: String? = "",
//    @SerializedName("aadharNomineePhotoDownloadUrl") @Expose var aadharNomineePhotoDownloadUrl: String? = "",
//    @SerializedName("nomineePhotoFileName") @Expose var nomineePhotoFileName: String? = "",
//    @SerializedName("nomineePhotoDownloadUrl") @Expose var nomineePhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = personMobileNo ?: ""
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class FoodLicense(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("businessName") @Expose var businessName: String? = "",
    @SerializedName("businessAddress") @Expose var businessAddress: String? = "",
    @SerializedName("ownerName") @Expose var ownerName: String? = "",
    @SerializedName("ownerMobileNo") @Expose var ownerMobileNo: String? = "",
    @SerializedName("ownerEmail") @Expose var ownerEmail: String? = "",
    @SerializedName("yearsLicense") @Expose var yearsLicense: String? = "",
    @SerializedName("productSales") @Expose var productSales: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("addressProofPhotoFileName") @Expose var addressProofPhotoFileName: String? = "",
    @SerializedName("addressProofPhotoDownloadUrl") @Expose var addressProofPhotoDownloadUrl: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
//    @SerializedName("gpnocPhotoFileName") @Expose var gpnocPhotoFileName: String? = "",
//    @SerializedName("gpnocPhotoDownloadUrl") @Expose var gpnocPhotoDownloadUrl: String? = "",
    @SerializedName("photoFileName") @Expose var photoFileName: String? = "",
    @SerializedName("photoDownloadUrl") @Expose var photoDownloadUrl: String? = "",
    @SerializedName("signPhotoFileName") @Expose var signPhotoFileName: String? = "",
    @SerializedName("signPhotoDownloadUrl") @Expose var signPhotoDownloadUrl: String? = "",
    @SerializedName("udyamAadharPhotoFileName") @Expose var udyamAadharPhotoFileName: String? = "",
    @SerializedName("udyamAadharPhotoDownloadUrl") @Expose var udyamAadharPhotoDownloadUrl: String? = "",
    @SerializedName("oldFoodLicensePhotoFileName") @Expose var oldFoodLicensePhotoFileName: String? = "",
    @SerializedName("oldFoodLicensePhotoDownloadUrl") @Expose var oldFoodLicensePhotoDownloadUrl: String? = "",
    @SerializedName("rcBookPhotoFileName") @Expose var rcBookPhotoFileName: String? = "",
    @SerializedName("rcBookPhotoDownloadUrl") @Expose var rcBookPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = ownerMobileNo ?: ""

    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class ManualAadharPanCard(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("photoFileName") @Expose var photoFileName: String? = "",
    @SerializedName("photoDownloadUrl") @Expose var photoDownloadUrl: String? = "",
    @SerializedName("signFileName") @Expose var signFileName: String? = "",
    @SerializedName("signDownloadUrl") @Expose var signDownloadUrl: String? = "",
    @SerializedName("aadhar_number") @Expose var aadhar_number: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("pan_number") @Expose var pan_number: String? = "",
    @SerializedName("father_name") @Expose var father_name: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
    @SerializedName("suvidhaType") @Expose override var suvidhaType: String = "",
) : Parcelable, ServiceModel {
    override var mobileNo: String = ""
}

@Parcelize
data class NepalMoneyTransfer(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("sender_name") @Expose var sender_name: String? = "",
    @SerializedName("receiver_name") @Expose var receiver_name: String? = "",
    @SerializedName("receiver_mobile") @Expose var receiver_mobile: String? = "",
    @SerializedName("receiver_address") @Expose var receiver_address: String? = "",
    @SerializedName("nepaleseCardFrontPhotoFileName") @Expose var nepaleseCardFrontPhotoFileName: String? = "",
    @SerializedName("nepaleseCardFrontPhotoDownloadUrl") @Expose var nepaleseCardFrontPhotoDownloadUrl: String? = "",
    @SerializedName("nepaleseCardBackPhotoFileName") @Expose var nepaleseCardBackPhotoFileName: String? = "",
    @SerializedName("nepaleseCardBackPhotoDownloadUrl") @Expose var nepaleseCardBackPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
    @SerializedName("suvidhaType") @Expose override var suvidhaType: String = "",
) : Parcelable, ServiceModel {
    override val applicantName: String
        get() = sender_name.toString()
    override val mobileNo: String
        get() = receiver_mobile.toString()
}

@Parcelize
data class RailwayTicketBooking(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("rail_booking_from") @Expose var rail_booking_from: String? = "",
    @SerializedName("rail_booking_destination") @Expose var rail_booking_destination: String? = "",
    @SerializedName("rail_booking_date") @Expose var rail_booking_date: String? = "",
    @SerializedName("rail_booking_name") @Expose var rail_booking_name: String? = "",
    @SerializedName("rail_booking_age") @Expose var rail_booking_age: String? = "",
    @SerializedName("rail_booking_mobileNo") @Expose var rail_booking_mobileNo: String? = "",
    @SerializedName("rail_booking_address") @Expose var rail_booking_address: String? = "",
    @SerializedName("aadharPhotoFileName") @Expose var aadharPhotoFileName: String? = "",
    @SerializedName("aadharPhotoDownloadUrl") @Expose var aadharPhotoDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
    @SerializedName("suvidhaType") @Expose override var suvidhaType: String = "",
) : Parcelable, ServiceModel {
    override val applicantName: String
        get() = rail_booking_name ?: ""
    override val mobileNo: String
        get() = rail_booking_mobileNo ?: ""
}

@Parcelize
data class BusinessPanCard(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("fullName") @Expose var fullName: String? = "",
    @SerializedName("fullAddress") @Expose var fullAddress: String? = "",
    @SerializedName("startDate") @Expose var startDate: String? = "",
    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("gpnocPhotoFileName") @Expose var gpnocPhotoFileName: String? = "",
    @SerializedName("gpnocPhotoDownloadUrl") @Expose var gpnocPhotoDownloadUrl: String? = "",
    @SerializedName("stampPhotoFileName") @Expose var stampPhotoFileName: String? = "",
    @SerializedName("stampPhotoDownloadUrl") @Expose var stampPhotoDownloadUrl: String? = "",
//    @SerializedName("regCertPhotoFileName") @Expose var regCertPhotoFileName: String? = "",
//    @SerializedName("regCertPhotoDownloadUrl") @Expose var regCertPhotoDownloadUrl: String? = "",
    @SerializedName("partnershipDeedPhotoFileName") @Expose var partnershipDeedPhotoFileName: String? = "",
    @SerializedName("partnershipDeedPhotoDownloadUrl") @Expose var partnershipDeedPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class ElectionCard(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("fullName") @Expose var fullName: String? = "",
    @SerializedName("customerEmail") @Expose var customerEmail: String? = "",
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String? = "",
    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("vidhansabha") @Expose var vidhansabha: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("electionCardFrontPhotoFileName") @Expose var electionCardFrontPhotoFileName: String? = "",
    @SerializedName("electionCardFrontPhotoDownloadUrl") @Expose var electionCardFrontPhotoDownloadUrl: String? = "",
    @SerializedName("electionCardBackPhotoFileName") @Expose var electionCardBackPhotoFileName: String? = "",
    @SerializedName("electionCardBackPhotoDownloadUrl") @Expose var electionCardBackPhotoDownloadUrl: String? = "",
    @SerializedName("oldElectionCardFrontPhotoFileName") @Expose var oldElectionCardFrontPhotoFileName: String? = "",
    @SerializedName("oldElectionCardFrontPhotoDownloadUrl") @Expose var oldElectionCardFrontPhotoDownloadUrl: String? = "",
    @SerializedName("oldElectionCardBackPhotoFileName") @Expose var oldElectionCardBackPhotoFileName: String? = "",
    @SerializedName("oldElectionCardBackPhotoDownloadUrl") @Expose var oldElectionCardBackPhotoDownloadUrl: String? = "",
    @SerializedName("correctionProofPhotoFileName") @Expose var correctionProofPhotoFileName: String? = "",
    @SerializedName("correctionProofPhotoDownloadUrl") @Expose var correctionProofPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
) : Parcelable, ServiceModel {
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class Passport(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String? = "",
    @SerializedName("dob") @Expose var dob: String? = "",
    @SerializedName("applicantEmail") @Expose var applicantEmail: String? = "",
    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("aadharNo") @Expose var aadharNo: String? = "",
    @SerializedName("birthPlace") @Expose var birthPlace: String? = "",
    @SerializedName("education") @Expose var education: String? = "",
    @SerializedName("employment") @Expose var employment: String? = "",
    @SerializedName("maritalStatus") @Expose var maritalStatus: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("fatherName") @Expose var fatherName: String? = "",
    @SerializedName("motherName") @Expose var motherName: String? = "",
    @SerializedName("emergencyName") @Expose var emergencyName: String? = "",
    @SerializedName("emergencyMobileNo") @Expose var emergencyMobileNo: String? = "",
    @SerializedName("husbandWifeName") @Expose var husbandWifeName: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("panFileName") @Expose var panFileName: String? = "",
    @SerializedName("panDownloadUrl") @Expose var panDownloadUrl: String? = "",
    @SerializedName("oldPassportPhotoFileName") @Expose var oldPassportPhotoFileName: String? = "",
    @SerializedName("oldPassportPhotoDownloadUrl") @Expose var oldPassportPhotoDownloadUrl: String? = "",
    @SerializedName("oldPassportFileNoPhotoFileName") @Expose var oldPassportFileNoPhotoFileName: String? = "",
    @SerializedName("oldPassportFileNoPhotoDownloadUrl") @Expose var oldPassportFileNoPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class DrivingLearningLicense(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String? = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("learningLicenseNo") @Expose var learningLicenseNo: String? = "",
    @SerializedName("applicantEmail") @Expose var applicantEmail: String? = "",
    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("aadharNo") @Expose var aadharNo: String? = "",
    @SerializedName("education") @Expose var education: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("fatherName") @Expose var fatherName: String? = "",
    @SerializedName("rto") @Expose var rto: String? = "",
    @SerializedName("license") @Expose var license: String? = "",
    @SerializedName("dob") @Expose var dob: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("signPhotoFileName") @Expose var signPhotoFileName: String? = "",
    @SerializedName("signPhotoDownloadUrl") @Expose var signPhotoDownloadUrl: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("learningLicensePhotoFileName") @Expose var learningLicensePhotoFileName: String? = "",
    @SerializedName("learningLicensePhotoDownloadUrl") @Expose var learningLicensePhotoDownloadUrl: String? = "",
    @SerializedName("oldFixedPhotoFileName") @Expose var oldFixedPhotoFileName: String? = "",
    @SerializedName("oldFixedPhotoDownloadUrl") @Expose var oldFixedPhotoDownloadUrl: String? = "",
    @SerializedName("nocPhotoFileName") @Expose var nocPhotoFileName: String? = "",
    @SerializedName("nocPhotoDownloadUrl") @Expose var nocPhotoDownloadUrl: String? = "",
    @SerializedName("rcBookPhotoFileName") @Expose var rcBookPhotoFileName: String? = "",
    @SerializedName("rcBookPhotoDownloadUrl") @Expose var rcBookPhotoDownloadUrl: String? = "",
    @SerializedName("insurancePhotoFileName") @Expose var insurancePhotoFileName: String? = "",
    @SerializedName("insurancePhotoDownloadUrl") @Expose var insurancePhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
    // Generic dynamic-type fields (populated for records created after the dynamic-types conversion).
    @SerializedName("serviceType") @Expose var serviceType: String = "",
    @SerializedName("serviceTypeToShow") @Expose var serviceTypeToShow: String = "",
    @SerializedName("textbox1") @Expose var textbox1: String? = "",
    @SerializedName("textbox1Hint") @Expose var textbox1Hint: String? = "",
    @SerializedName("textbox2") @Expose var textbox2: String? = "",
    @SerializedName("textbox2Hint") @Expose var textbox2Hint: String? = "",
    @SerializedName("textbox3") @Expose var textbox3: String? = "",
    @SerializedName("textbox3Hint") @Expose var textbox3Hint: String? = "",
    @SerializedName("textbox4") @Expose var textbox4: String? = "",
    @SerializedName("textbox4Hint") @Expose var textbox4Hint: String? = "",
    @SerializedName("textbox5") @Expose var textbox5: String? = "",
    @SerializedName("textbox5Hint") @Expose var textbox5Hint: String? = "",
    @SerializedName("datebox") @Expose var datebox: String? = "",
    @SerializedName("dateboxHint") @Expose var dateboxHint: String? = "",
    @SerializedName("attachment1Hint") @Expose var attachment1Hint: String? = "",
    @SerializedName("attachment1FileName") @Expose var attachment1FileName: String? = "",
    @SerializedName("attachment1DownloadUrl") @Expose var attachment1DownloadUrl: String? = "",
    @SerializedName("attachment2Hint") @Expose var attachment2Hint: String? = "",
    @SerializedName("attachment2FileName") @Expose var attachment2FileName: String? = "",
    @SerializedName("attachment2DownloadUrl") @Expose var attachment2DownloadUrl: String? = "",
    @SerializedName("attachment3Hint") @Expose var attachment3Hint: String? = "",
    @SerializedName("attachment3FileName") @Expose var attachment3FileName: String? = "",
    @SerializedName("attachment3DownloadUrl") @Expose var attachment3DownloadUrl: String? = "",
    @SerializedName("attachment4Hint") @Expose var attachment4Hint: String? = "",
    @SerializedName("attachment4FileName") @Expose var attachment4FileName: String? = "",
    @SerializedName("attachment4DownloadUrl") @Expose var attachment4DownloadUrl: String? = "",
    @SerializedName("attachment5Hint") @Expose var attachment5Hint: String? = "",
    @SerializedName("attachment5FileName") @Expose var attachment5FileName: String? = "",
    @SerializedName("attachment5DownloadUrl") @Expose var attachment5DownloadUrl: String? = "",
    @SerializedName("attachment6Hint") @Expose var attachment6Hint: String? = "",
    @SerializedName("attachment6FileName") @Expose var attachment6FileName: String? = "",
    @SerializedName("attachment6DownloadUrl") @Expose var attachment6DownloadUrl: String? = "",
    @SerializedName("attachment7Hint") @Expose var attachment7Hint: String? = "",
    @SerializedName("attachment7FileName") @Expose var attachment7FileName: String? = "",
    @SerializedName("attachment7DownloadUrl") @Expose var attachment7DownloadUrl: String? = "",
    @SerializedName("attachment8Hint") @Expose var attachment8Hint: String? = "",
    @SerializedName("attachment8FileName") @Expose var attachment8FileName: String? = "",
    @SerializedName("attachment8DownloadUrl") @Expose var attachment8DownloadUrl: String? = "",
    @SerializedName("attachment9Hint") @Expose var attachment9Hint: String? = "",
    @SerializedName("attachment9FileName") @Expose var attachment9FileName: String? = "",
    @SerializedName("attachment9DownloadUrl") @Expose var attachment9DownloadUrl: String? = "",
    @SerializedName("attachment10Hint") @Expose var attachment10Hint: String? = "",
    @SerializedName("attachment10FileName") @Expose var attachment10FileName: String? = "",
    @SerializedName("attachment10DownloadUrl") @Expose var attachment10DownloadUrl: String? = "",
) : Parcelable, ServiceModel {
    override val suvidhaType: String
        get() = typeToShow ?: ""
}
@Parcelize
data class EditPanAadharCard(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("panOrAadharCard") @Expose var panOrAadharCard: String? = "",
    @SerializedName("panNo") @Expose var panNo: String? = "",
    @SerializedName("fullName") @Expose var fullName: String? = "",
    @SerializedName("fatherName") @Expose var fatherName: String? = "",
    @SerializedName("dob") @Expose var dob: String? = "",
    @SerializedName("aadharNo") @Expose var aadharNo: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("oldPanCardPhotoFileName") @Expose var oldPanCardPhotoFileName: String? = "",
    @SerializedName("oldPanCardPhotoDownloadUrl") @Expose var oldPanCardPhotoDownloadUrl: String? = "",
    @SerializedName("oldAadharCardPhotoFileName") @Expose var oldAadharCardPhotoFileName: String? = "",
    @SerializedName("oldAadharCardPhotoDownloadUrl") @Expose var oldAadharCardPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
) : Parcelable, ServiceModel {
    override var mobileNo: String = ""
    override val applicantName: String
        get() =  fullName ?: ""
    override val suvidhaType: String
        get() = panOrAadharCard ?: ""
}
@Parcelize
data class PoliceVerification(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String? = "",
    @SerializedName("fullName") @Expose var fullName: String? = "",
    @SerializedName("fatherName") @Expose var fatherName: String? = "",
//    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("userEmail") @Expose var userEmail: String? = "",
    @SerializedName("commissionerOffice") @Expose var commissionerOffice: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("addressPoliceStation") @Expose var addressPoliceStation: String? = "",
    @SerializedName("formerAddress") @Expose var formerAddress: String? = "",
    @SerializedName("acquaintanceName1") @Expose var acquaintanceName1: String? = "",
    @SerializedName("acquaintanceMobileNo1") @Expose var acquaintanceMobileNo1: String? = "",
    @SerializedName("acquaintanceAddress1") @Expose var acquaintanceAddress1: String? = "",
    @SerializedName("acquaintanceName2") @Expose var acquaintanceName2: String? = "",
    @SerializedName("acquaintanceMobileNo2") @Expose var acquaintanceMobileNo2: String? = "",
    @SerializedName("acquaintanceAddress2") @Expose var acquaintanceAddress2: String? = "",
    @SerializedName("certificatePurpose") @Expose var certificatePurpose: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("rationFrontPhotoFileName") @Expose var rationFrontPhotoFileName: String? = "",
    @SerializedName("rationFrontPhotoDownloadUrl") @Expose var rationFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationBackPhotoFileName") @Expose var rationBackPhotoFileName: String? = "",
    @SerializedName("rationBackPhotoDownloadUrl") @Expose var rationBackPhotoDownloadUrl: String? = "",
    @SerializedName("bonafideFileName") @Expose var bonafideFileName: String? = "",
    @SerializedName("bonafideDownloadUrl") @Expose var bonafideDownloadUrl: String? = "",
    @SerializedName("companyLetterFileName") @Expose var companyLetterFileName: String? = "",
    @SerializedName("companyLetterDownloadUrl") @Expose var companyLetterDownloadUrl: String? = "",
    @SerializedName("signFileName") @Expose var signFileName: String? = "",
    @SerializedName("signDownloadUrl") @Expose var signDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("textbox1") @Expose var textbox1: String? = "",
    @SerializedName("textbox1Hint") @Expose var textbox1Hint: String? = "",
    @SerializedName("textbox2") @Expose var textbox2: String? = "",
    @SerializedName("textbox2Hint") @Expose var textbox2Hint: String? = "",
    @SerializedName("textbox3") @Expose var textbox3: String? = "",
    @SerializedName("textbox3Hint") @Expose var textbox3Hint: String? = "",
    @SerializedName("textbox4") @Expose var textbox4: String? = "",
    @SerializedName("textbox4Hint") @Expose var textbox4Hint: String? = "",
    @SerializedName("textbox5") @Expose var textbox5: String? = "",
    @SerializedName("textbox5Hint") @Expose var textbox5Hint: String? = "",
    @SerializedName("datebox") @Expose var datebox: String? = "",
    @SerializedName("dateboxHint") @Expose var dateboxHint: String? = "",
    @SerializedName("attachment1Hint") @Expose var attachment1Hint: String? = "",
    @SerializedName("attachment1FileName") @Expose var attachment1FileName: String? = "",
    @SerializedName("attachment1DownloadUrl") @Expose var attachment1DownloadUrl: String? = "",
    @SerializedName("attachment2Hint") @Expose var attachment2Hint: String? = "",
    @SerializedName("attachment2FileName") @Expose var attachment2FileName: String? = "",
    @SerializedName("attachment2DownloadUrl") @Expose var attachment2DownloadUrl: String? = "",
    @SerializedName("attachment3Hint") @Expose var attachment3Hint: String? = "",
    @SerializedName("attachment3FileName") @Expose var attachment3FileName: String? = "",
    @SerializedName("attachment3DownloadUrl") @Expose var attachment3DownloadUrl: String? = "",
    @SerializedName("attachment4Hint") @Expose var attachment4Hint: String? = "",
    @SerializedName("attachment4FileName") @Expose var attachment4FileName: String? = "",
    @SerializedName("attachment4DownloadUrl") @Expose var attachment4DownloadUrl: String? = "",
    @SerializedName("attachment5Hint") @Expose var attachment5Hint: String? = "",
    @SerializedName("attachment5FileName") @Expose var attachment5FileName: String? = "",
    @SerializedName("attachment5DownloadUrl") @Expose var attachment5DownloadUrl: String? = "",
    @SerializedName("attachment6Hint") @Expose var attachment6Hint: String? = "",
    @SerializedName("attachment6FileName") @Expose var attachment6FileName: String? = "",
    @SerializedName("attachment6DownloadUrl") @Expose var attachment6DownloadUrl: String? = "",
    @SerializedName("attachment7Hint") @Expose var attachment7Hint: String? = "",
    @SerializedName("attachment7FileName") @Expose var attachment7FileName: String? = "",
    @SerializedName("attachment7DownloadUrl") @Expose var attachment7DownloadUrl: String? = "",
    @SerializedName("attachment8Hint") @Expose var attachment8Hint: String? = "",
    @SerializedName("attachment8FileName") @Expose var attachment8FileName: String? = "",
    @SerializedName("attachment8DownloadUrl") @Expose var attachment8DownloadUrl: String? = "",
    @SerializedName("attachment9Hint") @Expose var attachment9Hint: String? = "",
    @SerializedName("attachment9FileName") @Expose var attachment9FileName: String? = "",
    @SerializedName("attachment9DownloadUrl") @Expose var attachment9DownloadUrl: String? = "",
    @SerializedName("attachment10Hint") @Expose var attachment10Hint: String? = "",
    @SerializedName("attachment10FileName") @Expose var attachment10FileName: String? = "",
    @SerializedName("attachment10DownloadUrl") @Expose var attachment10DownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = textbox3 ?: ""
    override val applicantName: String
        get() = textbox1 ?: ""
    override val suvidhaType: String
        get() = typeToShow ?: ""
}
@Parcelize
data class IncomeCertificate(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("fullName") @Expose var fullName: String? = "",
    @SerializedName("fatherName") @Expose var fatherName: String? = "",
    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("userEmail") @Expose var userEmail: String? = "",
    @SerializedName("commissionerOffice") @Expose var commissionerOffice: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("certificatePurpose") @Expose var certificatePurpose: String? = "",
    @SerializedName("dob") @Expose var dob: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("rationFrontPhotoFileName") @Expose var rationFrontPhotoFileName: String? = "",
    @SerializedName("rationFrontPhotoDownloadUrl") @Expose var rationFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationBackPhotoFileName") @Expose var rationBackPhotoFileName: String? = "",
    @SerializedName("rationBackPhotoDownloadUrl") @Expose var rationBackPhotoDownloadUrl: String? = "",
    @SerializedName("bonafideFileName") @Expose var bonafideFileName: String? = "",
    @SerializedName("bonafideDownloadUrl") @Expose var bonafideDownloadUrl: String? = "",
    @SerializedName("talathiResidentCertFileName") @Expose var talathiResidentCertFileName: String? = "",
    @SerializedName("talathiResidentCertDownloadUrl") @Expose var talathiResidentCertDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
    @SerializedName("suvidhaType") @Expose override var suvidhaType: String = "",
) : Parcelable, ServiceModel {
    override val applicantName: String
        get() = fullName ?: ""
}
@Parcelize
data class AgeCertificate(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("fullName") @Expose var fullName: String? = "",
    @SerializedName("fatherName") @Expose var fatherName: String? = "",
    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("userEmail") @Expose var userEmail: String? = "",
    @SerializedName("commissionerOffice") @Expose var commissionerOffice: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("certificatePurpose") @Expose var certificatePurpose: String? = "",
    @SerializedName("dob") @Expose var dob: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("rationFrontPhotoFileName") @Expose var rationFrontPhotoFileName: String? = "",
    @SerializedName("rationFrontPhotoDownloadUrl") @Expose var rationFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationBackPhotoFileName") @Expose var rationBackPhotoFileName: String? = "",
    @SerializedName("rationBackPhotoDownloadUrl") @Expose var rationBackPhotoDownloadUrl: String? = "",
    @SerializedName("bonafideFileName") @Expose var bonafideFileName: String? = "",
    @SerializedName("bonafideDownloadUrl") @Expose var bonafideDownloadUrl: String? = "",
    @SerializedName("talathiResidentCertFileName") @Expose var talathiResidentCertFileName: String? = "",
    @SerializedName("talathiResidentCertDownloadUrl") @Expose var talathiResidentCertDownloadUrl: String? = "",
    @SerializedName("oldLightBillFileName") @Expose var oldLightBillFileName: String? = "",
    @SerializedName("oldLightBillDownloadUrl") @Expose var oldLightBillDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
    @SerializedName("suvidhaType") @Expose override var suvidhaType: String = "",
) : Parcelable, ServiceModel {
    override val applicantName: String
        get() = fullName ?: ""
}
@Parcelize
data class Gazzet(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("fullName") @Expose var fullName: String? = "",
    @SerializedName("fatherName") @Expose var fatherName: String? = "",
    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("userEmail") @Expose var userEmail: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("certificatePurpose") @Expose var certificatePurpose: String? = "",
    @SerializedName("category") @Expose var category: String? = "",
    @SerializedName("categoryToShow") @Expose var categoryToShow: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("rationFrontPhotoFileName") @Expose var rationFrontPhotoFileName: String? = "",
    @SerializedName("rationFrontPhotoDownloadUrl") @Expose var rationFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationBackPhotoFileName") @Expose var rationBackPhotoFileName: String? = "",
    @SerializedName("rationBackPhotoDownloadUrl") @Expose var rationBackPhotoDownloadUrl: String? = "",
    @SerializedName("bonafideFileName") @Expose var bonafideFileName: String? = "",
    @SerializedName("bonafideDownloadUrl") @Expose var bonafideDownloadUrl: String? = "",
    @SerializedName("signPhotoFileName") @Expose var signPhotoFileName: String? = "",
    @SerializedName("signPhotoDownloadUrl") @Expose var signPhotoDownloadUrl: String? = "",
    @SerializedName("stampHundredPhotoFileName") @Expose var stampHundredPhotoFileName: String? = "",
    @SerializedName("stampHundredPhotoDownloadUrl") @Expose var stampHundredPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val applicantName: String
        get() = fullName ?: ""
    override val suvidhaType: String
        get() = categoryToShow ?: ""
}
@Parcelize
data class JyotishShastra(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String? = "",
    @SerializedName("serviceType") @Expose var serviceType: String = "",
    @SerializedName("fullName") @Expose var fullName: String? = "",
    @SerializedName("dob") @Expose var dob: String? = "",
    @SerializedName("birthtime") @Expose var birthtime: String? = "",
    @SerializedName("birthtimeAmPm") @Expose var birthtimeAmPm: String? = "",
    @SerializedName("birthplace") @Expose var birthplace: String? = "",
    @SerializedName("girlFullName") @Expose var girlFullName: String? = "",
    @SerializedName("girlDob") @Expose var girlDob: String? = "",
    @SerializedName("girlBirthTime") @Expose var girlBirthTime: String? = "",
    @SerializedName("girlBirthTimeAmPm") @Expose var girlBirthTimeAmPm: String? = "",
    @SerializedName("girlBirthPlace") @Expose var girlBirthPlace: String? = "",
    @SerializedName("question") @Expose var question: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
    @SerializedName("suvidhaType") @Expose override var suvidhaType: String = "",
) : Parcelable, ServiceModel {
    override var mobileNo: String = ""
    override val applicantName: String
        get() = fullName ?: ""
}
@Parcelize
data class CibilReport(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String? = "",
    @SerializedName("customerName") @Expose var customerName: String? = "",
    @SerializedName("customerMobileNo") @Expose var customerMobileNo: String? = "",
    @SerializedName("customerEmail") @Expose var customerEmail: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = customerMobileNo ?: ""
    override val applicantName: String
        get() = customerName.toString()
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class CreditCard(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("customerName") @Expose var customerName: String? = "",
    @SerializedName("customerMobileNo") @Expose var customerMobileNo: String? = "",
    @SerializedName("customerEmail") @Expose var customerEmail: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("latestPaymentSlipFileName") @Expose var latestPaymentSlipFileName: String? = "",
    @SerializedName("latestPaymentSlipDownloadUrl") @Expose var latestPaymentSlipDownloadUrl: String? = "",
    @SerializedName("incomeTaxReturnFileName") @Expose var incomeTaxReturnFileName: String? = "",
    @SerializedName("incomeTaxReturnDownloadUrl") @Expose var incomeTaxReturnDownloadUrl: String? = "",
    @SerializedName("bankOldCardStatementFileName") @Expose var bankOldCardStatementFileName: String? = "",
    @SerializedName("bankOldCardStatementDownloadUrl") @Expose var bankOldCardStatementDownloadUrl: String? = "",
    @SerializedName("textbox1") @Expose var textbox1: String? = "",
    @SerializedName("textbox1Hint") @Expose var textbox1Hint: String? = "",
    @SerializedName("textbox2") @Expose var textbox2: String? = "",
    @SerializedName("textbox2Hint") @Expose var textbox2Hint: String? = "",
    @SerializedName("textbox3") @Expose var textbox3: String? = "",
    @SerializedName("textbox3Hint") @Expose var textbox3Hint: String? = "",
    @SerializedName("textbox4") @Expose var textbox4: String? = "",
    @SerializedName("textbox4Hint") @Expose var textbox4Hint: String? = "",
    @SerializedName("textbox5") @Expose var textbox5: String? = "",
    @SerializedName("textbox5Hint") @Expose var textbox5Hint: String? = "",
    @SerializedName("datebox") @Expose var datebox: String? = "",
    @SerializedName("dateboxHint") @Expose var dateboxHint: String? = "",
    @SerializedName("attachment1Hint") @Expose var attachment1Hint: String? = "",
    @SerializedName("attachment1FileName") @Expose var attachment1FileName: String? = "",
    @SerializedName("attachment1DownloadUrl") @Expose var attachment1DownloadUrl: String? = "",
    @SerializedName("attachment2Hint") @Expose var attachment2Hint: String? = "",
    @SerializedName("attachment2FileName") @Expose var attachment2FileName: String? = "",
    @SerializedName("attachment2DownloadUrl") @Expose var attachment2DownloadUrl: String? = "",
    @SerializedName("attachment3Hint") @Expose var attachment3Hint: String? = "",
    @SerializedName("attachment3FileName") @Expose var attachment3FileName: String? = "",
    @SerializedName("attachment3DownloadUrl") @Expose var attachment3DownloadUrl: String? = "",
    @SerializedName("attachment4Hint") @Expose var attachment4Hint: String? = "",
    @SerializedName("attachment4FileName") @Expose var attachment4FileName: String? = "",
    @SerializedName("attachment4DownloadUrl") @Expose var attachment4DownloadUrl: String? = "",
    @SerializedName("attachment5Hint") @Expose var attachment5Hint: String? = "",
    @SerializedName("attachment5FileName") @Expose var attachment5FileName: String? = "",
    @SerializedName("attachment5DownloadUrl") @Expose var attachment5DownloadUrl: String? = "",
    @SerializedName("attachment6Hint") @Expose var attachment6Hint: String? = "",
    @SerializedName("attachment6FileName") @Expose var attachment6FileName: String? = "",
    @SerializedName("attachment6DownloadUrl") @Expose var attachment6DownloadUrl: String? = "",
    @SerializedName("attachment7Hint") @Expose var attachment7Hint: String? = "",
    @SerializedName("attachment7FileName") @Expose var attachment7FileName: String? = "",
    @SerializedName("attachment7DownloadUrl") @Expose var attachment7DownloadUrl: String? = "",
    @SerializedName("attachment8Hint") @Expose var attachment8Hint: String? = "",
    @SerializedName("attachment8FileName") @Expose var attachment8FileName: String? = "",
    @SerializedName("attachment8DownloadUrl") @Expose var attachment8DownloadUrl: String? = "",
    @SerializedName("attachment9Hint") @Expose var attachment9Hint: String? = "",
    @SerializedName("attachment9FileName") @Expose var attachment9FileName: String? = "",
    @SerializedName("attachment9DownloadUrl") @Expose var attachment9DownloadUrl: String? = "",
    @SerializedName("attachment10Hint") @Expose var attachment10Hint: String? = "",
    @SerializedName("attachment10FileName") @Expose var attachment10FileName: String? = "",
    @SerializedName("attachment10DownloadUrl") @Expose var attachment10DownloadUrl: String? = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
    @SerializedName("serviceType") @Expose var serviceType: String = "",
    @SerializedName("suvidhaType") @Expose override var suvidhaType: String = "",
) : Parcelable, ServiceModel {
    override val applicantName: String
        get() = textbox1 ?: ""
    override val mobileNo: String
        get() = textbox3 ?: ""
}

@Parcelize
data class GstRegistration(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("businessName") @Expose var businessName: String? = "",
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String? = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("customerMobileNo") @Expose var customerMobileNo: String? = "",
    @SerializedName("customerEmail") @Expose var customerEmail: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("companyPanCardPhotoFileName") @Expose var companyPanCardPhotoFileName: String? = "",
    @SerializedName("companyPanCardPhotoDownloadUrl") @Expose var companyPanCardPhotoDownloadUrl: String? = "",
    @SerializedName("onlineRentalAgreeFileName") @Expose var onlineRentalAgreeFileName: String? = "",
    @SerializedName("onlineRentalAgreeDownloadUrl") @Expose var onlineRentalAgreeDownloadUrl: String? = "",
    @SerializedName("lightBillFileName") @Expose var lightBillFileName: String? = "",
    @SerializedName("lightBillDownloadUrl") @Expose var lightBillDownloadUrl: String? = "",
    @SerializedName("partnershipAgreeFileName") @Expose var partnershipAgreeFileName: String? = "",
    @SerializedName("partnershipAgreeDownloadUrl") @Expose var partnershipAgreeDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("udyamAadharFileName") @Expose var udyamAadharFileName: String? = "",
    @SerializedName("udyamAadharDownloadUrl") @Expose var udyamAadharDownloadUrl: String? = "",
    @SerializedName("partnershipDeedFileName") @Expose var partnershipDeedFileName: String? = "",
    @SerializedName("partnershipDeedDownloadUrl") @Expose var partnershipDeedDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
//    @SerializedName("serviceType") @Expose override var serviceType: String = "",
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = customerMobileNo ?: ""
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class AllGovtCard(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("serviceType") @Expose var serviceType: String = "",
    @SerializedName("serviceTypeToShow") @Expose var serviceTypeToShow: String = "",
//    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("aadharLinkedMobileNo") @Expose var aadharLinkedMobileNo: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("bankPassbookPhotoFileName") @Expose var bankPassbookPhotoFileName: String? = "",
    @SerializedName("bankPassbookPhotoDownloadUrl") @Expose var bankPassbookPhotoDownloadUrl: String? = "",
    @SerializedName("rationFrontPhotoFileName") @Expose var rationFrontPhotoFileName: String? = "",
    @SerializedName("rationFrontPhotoDownloadUrl") @Expose var rationFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationBackPhotoFileName") @Expose var rationBackPhotoFileName: String? = "",
    @SerializedName("rationBackPhotoDownloadUrl") @Expose var rationBackPhotoDownloadUrl: String? = "",
    @SerializedName("rationElectionFrontPhotoFileName") @Expose var rationElectionFrontPhotoFileName: String? = "",
    @SerializedName("rationElectionFrontPhotoDownloadUrl") @Expose var rationElectionFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationElectionBackPhotoFileName") @Expose var rationElectionBackPhotoFileName: String? = "",
    @SerializedName("rationElectionBackPhotoDownloadUrl") @Expose var rationElectionBackPhotoDownloadUrl: String? = "",
    @SerializedName("panNo") @Expose var panNo: String? = "",
    @SerializedName("customerEmail") @Expose var customerEmail: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("textbox1") @Expose var textbox1: String? = "",
    @SerializedName("textbox1Hint") @Expose var textbox1Hint: String? = "",
    @SerializedName("textbox2") @Expose var textbox2: String? = "",
    @SerializedName("textbox2Hint") @Expose var textbox2Hint: String? = "",
    @SerializedName("textbox3") @Expose var textbox3: String? = "",
    @SerializedName("textbox3Hint") @Expose var textbox3Hint: String? = "",
    @SerializedName("textbox4") @Expose var textbox4: String? = "",
    @SerializedName("textbox4Hint") @Expose var textbox4Hint: String? = "",
    @SerializedName("textbox5") @Expose var textbox5: String? = "",
    @SerializedName("textbox5Hint") @Expose var textbox5Hint: String? = "",
    @SerializedName("datebox") @Expose var datebox: String? = "",
    @SerializedName("dateboxHint") @Expose var dateboxHint: String? = "",
    @SerializedName("attachment1Hint") @Expose var attachment1Hint: String? = "",
    @SerializedName("attachment1FileName") @Expose var attachment1FileName: String? = "",
    @SerializedName("attachment1DownloadUrl") @Expose var attachment1DownloadUrl: String? = "",
    @SerializedName("attachment2Hint") @Expose var attachment2Hint: String? = "",
    @SerializedName("attachment2FileName") @Expose var attachment2FileName: String? = "",
    @SerializedName("attachment2DownloadUrl") @Expose var attachment2DownloadUrl: String? = "",
    @SerializedName("attachment3Hint") @Expose var attachment3Hint: String? = "",
    @SerializedName("attachment3FileName") @Expose var attachment3FileName: String? = "",
    @SerializedName("attachment3DownloadUrl") @Expose var attachment3DownloadUrl: String? = "",
    @SerializedName("attachment4Hint") @Expose var attachment4Hint: String? = "",
    @SerializedName("attachment4FileName") @Expose var attachment4FileName: String? = "",
    @SerializedName("attachment4DownloadUrl") @Expose var attachment4DownloadUrl: String? = "",
    @SerializedName("attachment5Hint") @Expose var attachment5Hint: String? = "",
    @SerializedName("attachment5FileName") @Expose var attachment5FileName: String? = "",
    @SerializedName("attachment5DownloadUrl") @Expose var attachment5DownloadUrl: String? = "",
    @SerializedName("attachment6Hint") @Expose var attachment6Hint: String? = "",
    @SerializedName("attachment6FileName") @Expose var attachment6FileName: String? = "",
    @SerializedName("attachment6DownloadUrl") @Expose var attachment6DownloadUrl: String? = "",
    @SerializedName("attachment7Hint") @Expose var attachment7Hint: String? = "",
    @SerializedName("attachment7FileName") @Expose var attachment7FileName: String? = "",
    @SerializedName("attachment7DownloadUrl") @Expose var attachment7DownloadUrl: String? = "",
    @SerializedName("attachment8Hint") @Expose var attachment8Hint: String? = "",
    @SerializedName("attachment8FileName") @Expose var attachment8FileName: String? = "",
    @SerializedName("attachment8DownloadUrl") @Expose var attachment8DownloadUrl: String? = "",
    @SerializedName("attachment9Hint") @Expose var attachment9Hint: String? = "",
    @SerializedName("attachment9FileName") @Expose var attachment9FileName: String? = "",
    @SerializedName("attachment9DownloadUrl") @Expose var attachment9DownloadUrl: String? = "",
    @SerializedName("attachment10Hint") @Expose var attachment10Hint: String? = "",
    @SerializedName("attachment10FileName") @Expose var attachment10FileName: String? = "",
    @SerializedName("attachment10DownloadUrl") @Expose var attachment10DownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = textbox3 ?: ""
    override val applicantName: String
        get() = textbox1 ?: ""
    override val suvidhaType: String
        get() = serviceTypeToShow ?: ""
}

@Parcelize
data class FarmerPolicy(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("serviceType") @Expose var serviceType: String = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("applicantNameHint") @Expose var applicantNameHint: String = "",
    @SerializedName("aadharLinkedMobileNo") @Expose var aadharLinkedMobileNo: String? = "",
    @SerializedName("aadharLinkedMobileNoHint") @Expose var aadharLinkedMobileNoHint: String? = "",
    @SerializedName("cropsToInsure") @Expose var cropsToInsure: String? = "",
    @SerializedName("customerEmail") @Expose var customerEmail: String? = "",
    @SerializedName("saatBaaraLinkMobileNo") @Expose var saatBaaraLinkMobileNo: String? = "",
    @SerializedName("saatBaaraLinkMobileNoHint") @Expose var saatBaaraLinkMobileNoHint: String? = "",
    @SerializedName("strTextbox4") @Expose var strTextbox4: String? = "",
    @SerializedName("strTextbox4Hint") @Expose var strTextbox4Hint: String? = "",
    @SerializedName("strTextbox5") @Expose var strTextbox5: String? = "",
    @SerializedName("strTextbox5Hint") @Expose var strTextbox5Hint: String? = "",
    @SerializedName("strDatebox") @Expose var strDatebox: String? = "",
    @SerializedName("strDateboxHint") @Expose var strDateboxHint: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("land8APhotoFileName") @Expose var land8APhotoFileName: String? = "",
    @SerializedName("land8APhotoDownloadUrl") @Expose var land8APhotoDownloadUrl: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
    @SerializedName("bankPassbookPhotoFileName") @Expose var bankPassbookPhotoFileName: String? = "",
    @SerializedName("bankPassbookPhotoDownloadUrl") @Expose var bankPassbookPhotoDownloadUrl: String? = "",
    @SerializedName("signSpecimenFileName") @Expose var signSpecimenFileName: String? = "",
    @SerializedName("signSpecimenDownloadUrl") @Expose var signSpecimenDownloadUrl: String? = "",
    @SerializedName("customer8APhotoFileName") @Expose var customer8APhotoFileName: String? = "",
    @SerializedName("customer8APhotoDownloadUrl") @Expose var customer8APhotoDownloadUrl: String? = "",
    @SerializedName("other8AOtherInfoFileName") @Expose var other8AOtherInfoFileName: String? = "",
    @SerializedName("other8AOtherInfoDownloadUrl") @Expose var other8AOtherInfoDownloadUrl: String? = "",
    @SerializedName("quotationFileName") @Expose var quotationFileName: String? = "",
    @SerializedName("quotationDownloadUrl") @Expose var quotationDownloadUrl: String? = "",
    @SerializedName("castCertFileName") @Expose var castCertFileName: String? = "",
    @SerializedName("castCertDownloadUrl") @Expose var castCertDownloadUrl: String? = "",
    @SerializedName("consentLetterFileName") @Expose var consentLetterFileName: String? = "",
    @SerializedName("consentLetterDownloadUrl") @Expose var consentLetterDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("aadharFrontPhotoHint") @Expose var aadharFrontPhotoHint: String? = "",
    @SerializedName("aadharBackPhotoHint") @Expose var aadharBackPhotoHint: String? = "",
    @SerializedName("land8APhotoHint") @Expose var land8APhotoHint: String? = "",
    @SerializedName("panPhotoHint") @Expose var panPhotoHint: String? = "",
    @SerializedName("customer8APhotoHint") @Expose var customer8APhotoHint: String? = "",
    @SerializedName("other8AOtherInfoHint") @Expose var other8AOtherInfoHint: String? = "",
    @SerializedName("quotationHint") @Expose var quotationHint: String? = "",
    @SerializedName("castCertHint") @Expose var castCertHint: String? = "",
    @SerializedName("consentLetterHint") @Expose var consentLetterHint: String? = "",
    @SerializedName("paymentScreenshotHint") @Expose var paymentScreenshotHint: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
) : Parcelable, ServiceModel {
    override var mobileNo: String = ""
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class AllOtherCard(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("serviceType") @Expose var serviceType: String = "",
    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("mobileNo") @Expose override var mobileNo: String = "",
    @SerializedName("applicantPhotoFileName") @Expose var applicantPhotoFileName: String? = "",
    @SerializedName("applicantPhotoDownloadUrl") @Expose var applicantPhotoDownloadUrl: String? = "",
    @SerializedName("gatNumber") @Expose var gatNumber: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("rationFrontPhotoFileName") @Expose var rationFrontPhotoFileName: String? = "",
    @SerializedName("rationFrontPhotoDownloadUrl") @Expose var rationFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationBackPhotoFileName") @Expose var rationBackPhotoFileName: String? = "",
    @SerializedName("rationBackPhotoDownloadUrl") @Expose var rationBackPhotoDownloadUrl: String? = "",
    @SerializedName("applicantSLCFileName") @Expose var applicantSLCFileName: String? = "",
    @SerializedName("applicantSLCDownloadUrl") @Expose var applicantSLCDownloadUrl: String? = "",
    @SerializedName("applicantCastCertXeroxFileName") @Expose var applicantCastCertXeroxFileName: String? = "",
    @SerializedName("applicantCastCertXeroxDownloadUrl") @Expose var applicantCastCertXeroxDownloadUrl: String? = "",
    @SerializedName("tehsildarIncomeCertFileName") @Expose var tehsildarIncomeCertFileName: String? = "",
    @SerializedName("tehsildarIncomeCertDownloadUrl") @Expose var tehsildarIncomeCertDownloadUrl: String? = "",
    @SerializedName("fatherSLCFileName") @Expose var fatherSLCFileName: String? = "",
    @SerializedName("fatherSLCDownloadUrl") @Expose var fatherSLCDownloadUrl: String? = "",
    @SerializedName("grandpaSLCFileName") @Expose var grandpaSLCFileName: String? = "",
    @SerializedName("grandpaSLCDownloadUrl") @Expose var grandpaSLCDownloadUrl: String? = "",
    @SerializedName("otherImpFileName") @Expose var otherImpFileName: String? = "",
    @SerializedName("otherImpDownloadUrl") @Expose var otherImpDownloadUrl: String? = "",
    @SerializedName("vanshavalFileName") @Expose var vanshavalFileName: String? = "",
    @SerializedName("vanshavalDownloadUrl") @Expose var vanshavalDownloadUrl: String? = "",
    @SerializedName("vanshavalPersonCastCertFileName") @Expose var vanshavalPersonCastCertFileName: String? = "",
    @SerializedName("vanshavalPersonCastCertDownloadUrl") @Expose var vanshavalPersonCastCertDownloadUrl: String? = "",
    @SerializedName("localEnquiryReportFileName") @Expose var localEnquiryReportFileName: String? = "",
    @SerializedName("localEnquiryReportDownloadUrl") @Expose var localEnquiryReportDownloadUrl: String? = "",
    @SerializedName("applicantSignSpecimenFileName") @Expose var applicantSignSpecimenFileName: String? = "",
    @SerializedName("applicantSignSpecimenDownloadUrl") @Expose var applicantSignSpecimenDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
    @SerializedName("suvidhaType") @Expose override var suvidhaType: String = "",
) : Parcelable, ServiceModel {
}

@Parcelize
data class DematAccount(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("customerName") @Expose var customerName: String? = "",
    @SerializedName("address") @Expose var address: String? = "",
    @SerializedName("customerMobileNo") @Expose var customerMobileNo: String? = "",
    @SerializedName("customerEmail") @Expose var customerEmail: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("lightBillFileName") @Expose var lightBillFileName: String? = "",
    @SerializedName("lightBillDownloadUrl") @Expose var lightBillDownloadUrl: String? = "",
    @SerializedName("bankPassbookFileName") @Expose var bankPassbookFileName: String? = "",
    @SerializedName("bankPassbookDownloadUrl") @Expose var bankPassbookDownloadUrl: String? = "",
    @SerializedName("otherDocsFileName") @Expose var otherDocsFileName: String? = "",
    @SerializedName("otherDocsDownloadUrl") @Expose var otherDocsDownloadUrl: String? = "",
    @SerializedName("textbox1") @Expose var textbox1: String? = "",
    @SerializedName("textbox1Hint") @Expose var textbox1Hint: String? = "",
    @SerializedName("textbox2") @Expose var textbox2: String? = "",
    @SerializedName("textbox2Hint") @Expose var textbox2Hint: String? = "",
    @SerializedName("textbox3") @Expose var textbox3: String? = "",
    @SerializedName("textbox3Hint") @Expose var textbox3Hint: String? = "",
    @SerializedName("textbox4") @Expose var textbox4: String? = "",
    @SerializedName("textbox4Hint") @Expose var textbox4Hint: String? = "",
    @SerializedName("textbox5") @Expose var textbox5: String? = "",
    @SerializedName("textbox5Hint") @Expose var textbox5Hint: String? = "",
    @SerializedName("datebox") @Expose var datebox: String? = "",
    @SerializedName("dateboxHint") @Expose var dateboxHint: String? = "",
    @SerializedName("attachment1Hint") @Expose var attachment1Hint: String? = "",
    @SerializedName("attachment1FileName") @Expose var attachment1FileName: String? = "",
    @SerializedName("attachment1DownloadUrl") @Expose var attachment1DownloadUrl: String? = "",
    @SerializedName("attachment2Hint") @Expose var attachment2Hint: String? = "",
    @SerializedName("attachment2FileName") @Expose var attachment2FileName: String? = "",
    @SerializedName("attachment2DownloadUrl") @Expose var attachment2DownloadUrl: String? = "",
    @SerializedName("attachment3Hint") @Expose var attachment3Hint: String? = "",
    @SerializedName("attachment3FileName") @Expose var attachment3FileName: String? = "",
    @SerializedName("attachment3DownloadUrl") @Expose var attachment3DownloadUrl: String? = "",
    @SerializedName("attachment4Hint") @Expose var attachment4Hint: String? = "",
    @SerializedName("attachment4FileName") @Expose var attachment4FileName: String? = "",
    @SerializedName("attachment4DownloadUrl") @Expose var attachment4DownloadUrl: String? = "",
    @SerializedName("attachment5Hint") @Expose var attachment5Hint: String? = "",
    @SerializedName("attachment5FileName") @Expose var attachment5FileName: String? = "",
    @SerializedName("attachment5DownloadUrl") @Expose var attachment5DownloadUrl: String? = "",
    @SerializedName("attachment6Hint") @Expose var attachment6Hint: String? = "",
    @SerializedName("attachment6FileName") @Expose var attachment6FileName: String? = "",
    @SerializedName("attachment6DownloadUrl") @Expose var attachment6DownloadUrl: String? = "",
    @SerializedName("attachment7Hint") @Expose var attachment7Hint: String? = "",
    @SerializedName("attachment7FileName") @Expose var attachment7FileName: String? = "",
    @SerializedName("attachment7DownloadUrl") @Expose var attachment7DownloadUrl: String? = "",
    @SerializedName("attachment8Hint") @Expose var attachment8Hint: String? = "",
    @SerializedName("attachment8FileName") @Expose var attachment8FileName: String? = "",
    @SerializedName("attachment8DownloadUrl") @Expose var attachment8DownloadUrl: String? = "",
    @SerializedName("attachment9Hint") @Expose var attachment9Hint: String? = "",
    @SerializedName("attachment9FileName") @Expose var attachment9FileName: String? = "",
    @SerializedName("attachment9DownloadUrl") @Expose var attachment9DownloadUrl: String? = "",
    @SerializedName("attachment10Hint") @Expose var attachment10Hint: String? = "",
    @SerializedName("attachment10FileName") @Expose var attachment10FileName: String? = "",
    @SerializedName("attachment10DownloadUrl") @Expose var attachment10DownloadUrl: String? = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
    @SerializedName("suvidhaType") @Expose override var suvidhaType: String = "",
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = textbox3 ?: ""

    override val applicantName: String
        get() = textbox1 ?: ""
}

@Parcelize
data class GovtScheme(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("serviceType") @Expose var serviceType: String = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("typeToShow") @Expose var typeToShow: String = "",
//    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("businessName") @Expose var businessName: String? = "",
    @SerializedName("businessAddress") @Expose var businessAddress: String? = "",
    @SerializedName("businessNature") @Expose var businessNature: String? = "",
    @SerializedName("ownerMobileNo") @Expose var ownerMobileNo: String,
    @SerializedName("ownerEmail") @Expose var ownerEmail: String? = "",
    @SerializedName("ownerCast") @Expose var ownerCast: String? = "",
    @SerializedName("loanAmt") @Expose var loanAmt: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
    @SerializedName("passbookPhotoFileName") @Expose var passbookPhotoFileName: String? = "",
    @SerializedName("passbookPhotoDownloadUrl") @Expose var passbookPhotoDownloadUrl: String? = "",
    @SerializedName("rationFrontPhotoFileName") @Expose var rationFrontPhotoFileName: String? = "",
    @SerializedName("rationFrontPhotoDownloadUrl") @Expose var rationFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationBackPhotoFileName") @Expose var rationBackPhotoFileName: String? = "",
    @SerializedName("rationBackPhotoDownloadUrl") @Expose var rationBackPhotoDownloadUrl: String? = "",
    @SerializedName("signPhotoFileName") @Expose var signPhotoFileName: String? = "",
    @SerializedName("signPhotoDownloadUrl") @Expose var signPhotoDownloadUrl: String? = "",
    @SerializedName("passportPhotoFileName") @Expose var passportPhotoFileName: String? = "",
    @SerializedName("passportPhotoDownloadUrl") @Expose var passportPhotoDownloadUrl: String? = "",
    @SerializedName("schoolLCFileName") @Expose var schoolLCFileName: String? = "",
    @SerializedName("schoolLCDownloadUrl") @Expose var schoolLCDownloadUrl: String? = "",
    @SerializedName("birthCertFileName") @Expose var birthCertFileName: String? = "",
    @SerializedName("birthCertDownloadUrl") @Expose var birthCertDownloadUrl: String? = "",
    @SerializedName("incomeCertFileName") @Expose var incomeCertFileName: String? = "",
    @SerializedName("incomeCertDownloadUrl") @Expose var incomeCertDownloadUrl: String? = "",
    @SerializedName("castCertFileName") @Expose var castCertFileName: String? = "",
    @SerializedName("castCertDownloadUrl") @Expose var castCertDownloadUrl: String? = "",
    @SerializedName("projectReportFileName") @Expose var projectReportFileName: String? = "",
    @SerializedName("projectReportDownloadUrl") @Expose var projectReportDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("textbox1") @Expose var textbox1: String? = "",
    @SerializedName("textbox1Hint") @Expose var textbox1Hint: String? = "",
    @SerializedName("textbox2") @Expose var textbox2: String? = "",
    @SerializedName("textbox2Hint") @Expose var textbox2Hint: String? = "",
    @SerializedName("textbox3") @Expose var textbox3: String? = "",
    @SerializedName("textbox3Hint") @Expose var textbox3Hint: String? = "",
    @SerializedName("textbox4") @Expose var textbox4: String? = "",
    @SerializedName("textbox4Hint") @Expose var textbox4Hint: String? = "",
    @SerializedName("textbox5") @Expose var textbox5: String? = "",
    @SerializedName("textbox5Hint") @Expose var textbox5Hint: String? = "",
    @SerializedName("datebox") @Expose var datebox: String? = "",
    @SerializedName("dateboxHint") @Expose var dateboxHint: String? = "",
    @SerializedName("attachment1Hint") @Expose var attachment1Hint: String? = "",
    @SerializedName("attachment1FileName") @Expose var attachment1FileName: String? = "",
    @SerializedName("attachment1DownloadUrl") @Expose var attachment1DownloadUrl: String? = "",
    @SerializedName("attachment2Hint") @Expose var attachment2Hint: String? = "",
    @SerializedName("attachment2FileName") @Expose var attachment2FileName: String? = "",
    @SerializedName("attachment2DownloadUrl") @Expose var attachment2DownloadUrl: String? = "",
    @SerializedName("attachment3Hint") @Expose var attachment3Hint: String? = "",
    @SerializedName("attachment3FileName") @Expose var attachment3FileName: String? = "",
    @SerializedName("attachment3DownloadUrl") @Expose var attachment3DownloadUrl: String? = "",
    @SerializedName("attachment4Hint") @Expose var attachment4Hint: String? = "",
    @SerializedName("attachment4FileName") @Expose var attachment4FileName: String? = "",
    @SerializedName("attachment4DownloadUrl") @Expose var attachment4DownloadUrl: String? = "",
    @SerializedName("attachment5Hint") @Expose var attachment5Hint: String? = "",
    @SerializedName("attachment5FileName") @Expose var attachment5FileName: String? = "",
    @SerializedName("attachment5DownloadUrl") @Expose var attachment5DownloadUrl: String? = "",
    @SerializedName("attachment6Hint") @Expose var attachment6Hint: String? = "",
    @SerializedName("attachment6FileName") @Expose var attachment6FileName: String? = "",
    @SerializedName("attachment6DownloadUrl") @Expose var attachment6DownloadUrl: String? = "",
    @SerializedName("attachment7Hint") @Expose var attachment7Hint: String? = "",
    @SerializedName("attachment7FileName") @Expose var attachment7FileName: String? = "",
    @SerializedName("attachment7DownloadUrl") @Expose var attachment7DownloadUrl: String? = "",
    @SerializedName("attachment8Hint") @Expose var attachment8Hint: String? = "",
    @SerializedName("attachment8FileName") @Expose var attachment8FileName: String? = "",
    @SerializedName("attachment8DownloadUrl") @Expose var attachment8DownloadUrl: String? = "",
    @SerializedName("attachment9Hint") @Expose var attachment9Hint: String? = "",
    @SerializedName("attachment9FileName") @Expose var attachment9FileName: String? = "",
    @SerializedName("attachment9DownloadUrl") @Expose var attachment9DownloadUrl: String? = "",
    @SerializedName("attachment10Hint") @Expose var attachment10Hint: String? = "",
    @SerializedName("attachment10FileName") @Expose var attachment10FileName: String? = "",
    @SerializedName("attachment10DownloadUrl") @Expose var attachment10DownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = textbox3 ?: ""
    override val applicantName: String
        get() = textbox1 ?: ""
    override val suvidhaType: String
        get() = typeToShow ?: ""
}

@Parcelize
data class Verification(
    @SerializedName("email") @Expose var email: String? = "",
    @SerializedName("createdBy") @Expose override var createdBy: String = "",
    @SerializedName("createdByUserName") @Expose var createdByUserName: String? = "",
    @SerializedName("uid") @Expose override var uid: String = "",
    @SerializedName("createdDateTime") @Expose override var createdDateTime: String = "",
    @SerializedName("type") @Expose var type: String = "",
    @SerializedName("serviceType") @Expose var serviceType: String = "",
    @SerializedName("serviceTypeToShow") @Expose var serviceTypeToShow: String = "",
//    @SerializedName("applicantName") @Expose override var applicantName: String = "",
    @SerializedName("aadharLinkedMobileNo") @Expose var aadharLinkedMobileNo: String? = "",
    @SerializedName("aadharFrontPhotoFileName") @Expose var aadharFrontPhotoFileName: String? = "",
    @SerializedName("aadharFrontPhotoDownloadUrl") @Expose var aadharFrontPhotoDownloadUrl: String? = "",
    @SerializedName("aadharBackPhotoFileName") @Expose var aadharBackPhotoFileName: String? = "",
    @SerializedName("aadharBackPhotoDownloadUrl") @Expose var aadharBackPhotoDownloadUrl: String? = "",
    @SerializedName("bankPassbookPhotoFileName") @Expose var bankPassbookPhotoFileName: String? = "",
    @SerializedName("bankPassbookPhotoDownloadUrl") @Expose var bankPassbookPhotoDownloadUrl: String? = "",
    @SerializedName("rationFrontPhotoFileName") @Expose var rationFrontPhotoFileName: String? = "",
    @SerializedName("rationFrontPhotoDownloadUrl") @Expose var rationFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationBackPhotoFileName") @Expose var rationBackPhotoFileName: String? = "",
    @SerializedName("rationBackPhotoDownloadUrl") @Expose var rationBackPhotoDownloadUrl: String? = "",
    @SerializedName("rationElectionFrontPhotoFileName") @Expose var rationElectionFrontPhotoFileName: String? = "",
    @SerializedName("rationElectionFrontPhotoDownloadUrl") @Expose var rationElectionFrontPhotoDownloadUrl: String? = "",
    @SerializedName("rationElectionBackPhotoFileName") @Expose var rationElectionBackPhotoFileName: String? = "",
    @SerializedName("rationElectionBackPhotoDownloadUrl") @Expose var rationElectionBackPhotoDownloadUrl: String? = "",
    @SerializedName("panNo") @Expose var panNo: String? = "",
    @SerializedName("customerEmail") @Expose var customerEmail: String? = "",
    @SerializedName("panPhotoFileName") @Expose var panPhotoFileName: String? = "",
    @SerializedName("panPhotoDownloadUrl") @Expose var panPhotoDownloadUrl: String? = "",
    @SerializedName("paymentScreenshotFileName") @Expose var paymentScreenshotFileName: String? = "",
    @SerializedName("paymentScreenshotDownloadUrl") @Expose var paymentScreenshotDownloadUrl: String? = "",
    @SerializedName("textbox1") @Expose var textbox1: String? = "",
    @SerializedName("textbox1Hint") @Expose var textbox1Hint: String? = "",
    @SerializedName("textbox2") @Expose var textbox2: String? = "",
    @SerializedName("textbox2Hint") @Expose var textbox2Hint: String? = "",
    @SerializedName("textbox3") @Expose var textbox3: String? = "",
    @SerializedName("textbox3Hint") @Expose var textbox3Hint: String? = "",
    @SerializedName("textbox4") @Expose var textbox4: String? = "",
    @SerializedName("textbox4Hint") @Expose var textbox4Hint: String? = "",
    @SerializedName("textbox5") @Expose var textbox5: String? = "",
    @SerializedName("textbox5Hint") @Expose var textbox5Hint: String? = "",
    @SerializedName("datebox") @Expose var datebox: String? = "",
    @SerializedName("dateboxHint") @Expose var dateboxHint: String? = "",
    @SerializedName("attachment1Hint") @Expose var attachment1Hint: String? = "",
    @SerializedName("attachment1FileName") @Expose var attachment1FileName: String? = "",
    @SerializedName("attachment1DownloadUrl") @Expose var attachment1DownloadUrl: String? = "",
    @SerializedName("attachment2Hint") @Expose var attachment2Hint: String? = "",
    @SerializedName("attachment2FileName") @Expose var attachment2FileName: String? = "",
    @SerializedName("attachment2DownloadUrl") @Expose var attachment2DownloadUrl: String? = "",
    @SerializedName("attachment3Hint") @Expose var attachment3Hint: String? = "",
    @SerializedName("attachment3FileName") @Expose var attachment3FileName: String? = "",
    @SerializedName("attachment3DownloadUrl") @Expose var attachment3DownloadUrl: String? = "",
    @SerializedName("attachment4Hint") @Expose var attachment4Hint: String? = "",
    @SerializedName("attachment4FileName") @Expose var attachment4FileName: String? = "",
    @SerializedName("attachment4DownloadUrl") @Expose var attachment4DownloadUrl: String? = "",
    @SerializedName("attachment5Hint") @Expose var attachment5Hint: String? = "",
    @SerializedName("attachment5FileName") @Expose var attachment5FileName: String? = "",
    @SerializedName("attachment5DownloadUrl") @Expose var attachment5DownloadUrl: String? = "",
    @SerializedName("attachment6Hint") @Expose var attachment6Hint: String? = "",
    @SerializedName("attachment6FileName") @Expose var attachment6FileName: String? = "",
    @SerializedName("attachment6DownloadUrl") @Expose var attachment6DownloadUrl: String? = "",
    @SerializedName("attachment7Hint") @Expose var attachment7Hint: String? = "",
    @SerializedName("attachment7FileName") @Expose var attachment7FileName: String? = "",
    @SerializedName("attachment7DownloadUrl") @Expose var attachment7DownloadUrl: String? = "",
    @SerializedName("attachment8Hint") @Expose var attachment8Hint: String? = "",
    @SerializedName("attachment8FileName") @Expose var attachment8FileName: String? = "",
    @SerializedName("attachment8DownloadUrl") @Expose var attachment8DownloadUrl: String? = "",
    @SerializedName("attachment9Hint") @Expose var attachment9Hint: String? = "",
    @SerializedName("attachment9FileName") @Expose var attachment9FileName: String? = "",
    @SerializedName("attachment9DownloadUrl") @Expose var attachment9DownloadUrl: String? = "",
    @SerializedName("attachment10Hint") @Expose var attachment10Hint: String? = "",
    @SerializedName("attachment10FileName") @Expose var attachment10FileName: String? = "",
    @SerializedName("attachment10DownloadUrl") @Expose var attachment10DownloadUrl: String? = "",
    @SerializedName("timeStamp") @Expose var timeStamp: Long?,
    @SerializedName("pushKey") @Expose var pushKey: String? = "",
    @SerializedName("isDelete") @Expose var isDelete: String? = "",
    @SerializedName("paymentStatus") @Expose override var paymentStatus: String = "", // 1 - paid, 2 - unpaid
    @SerializedName("closingUpdate") @Expose override var closingUpdate: String = "", // Complete, Refund Full, Refund Govt Fee, Cancel Unpaid
) : Parcelable, ServiceModel {
    override val mobileNo: String
        get() = textbox3 ?: ""
    override val applicantName: String
        get() = textbox1 ?: ""
    override val suvidhaType: String
        get() = serviceTypeToShow ?: ""
}
