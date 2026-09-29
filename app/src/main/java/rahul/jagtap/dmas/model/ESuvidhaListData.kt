package rahul.jagtap.dmas.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EsuvidhaInfo(
    var strDate: String? = null, var userUidList: ArrayList<UserUIDInfo>? = null
): Parcelable

@Parcelize
data class UserUIDInfo(
    var strUserUid: String? = null, var panCardList: ArrayList<PanCard>? = null,
    var shopActList: ArrayList<ShopAct>? = null,
    var udyamAadharList: ArrayList<UdyamAadhar>? = null,
    var foodLicenseList: ArrayList<FoodLicense>? = null,
    var providentFundList: ArrayList<ProvidentFund>? = null,
    var nepalMoneyTransferList: ArrayList<NepalMoneyTransfer>? = null,
    var railwayTicketBookingList: ArrayList<RailwayTicketBooking>? = null,
    var businessPanCardList: ArrayList<BusinessPanCard>? = null,
    var electionCardList: ArrayList<ElectionCard>? = null,
    var passportList: ArrayList<Passport>? = null,
    var drivingLearningLicenseList: ArrayList<DrivingLearningLicense>? = null,
    var verificationList: ArrayList<Verification>? = null,
    var editPanAadharCardList: ArrayList<EditPanAadharCard>? = null,
    var policeVerificationList: ArrayList<PoliceVerification>? = null,
    var incomeCertificateList: ArrayList<IncomeCertificate>? = null,
    var ageCertificateList: ArrayList<AgeCertificate>? = null,
    var gazzetList: ArrayList<Gazzet>? = null,
    var jyotishShastraList: ArrayList<JyotishShastra>? = null,
    var cibilReportList: ArrayList<CibilReport>? = null,
    var creditCardList: ArrayList<CreditCard>? = null,
    var gstRegistrationList: ArrayList<GstRegistration>? = null,
    var allGovtCardList: ArrayList<AllGovtCard>? = null,
    var farmerPolicyList: ArrayList<FarmerPolicy>? = null,
    var allOtherCardList: ArrayList<AllOtherCard>? = null,
    var dematAccountList: ArrayList<DematAccount>? = null,
    var govtSchemeList: ArrayList<GovtScheme>? = null,
): Parcelable


