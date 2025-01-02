
import * as functions from 'firebase-functions/v2/firestore';
import * as admin from 'firebase-admin';

const { Timestamp } = admin.firestore;

admin.initializeApp();

interface PaymentDetails {
  amountPaid?: number;
}

interface InvoiceData {
  totalAmount: number;
  paymentDetails?: PaymentDetails[];
  dueDate: FirebaseFirestore.Timestamp;
  paymentStatus?: string;
}

exports.updateSupplierInvoiceStatus = functions.onDocumentWritten(
  "users/{documentId}/supplierInvoice/{documentId}",
  async (event) => {
    const invoiceData = event.data?.after.data() as InvoiceData | undefined;
    if (!invoiceData) {
      console.log("Document deleted, no further action needed.");
      return;
    }

    const { totalAmount, paymentDetails, dueDate } = invoiceData;

    // Calculate the total amount paid
    const totalPaid = paymentDetails
      ? paymentDetails.reduce(
          (sum, payment) => sum + (payment.amountPaid || 0),
          0
        )
      : 0;

    const dueAmount = totalAmount - totalPaid;

    const now = Timestamp.now();

    let paymentStatus = "PENDING";
    if (dueAmount <= 0) {
      paymentStatus = "PAID";
    } else if (totalPaid > 0) {
        if(now.toMillis() > dueDate.toMillis()){
               paymentStatus = "OVERDUE";
       } else {
         paymentStatus = "PARTIALLY_PAID";
          }
    }

    // Update the Firestore document if the payment status has changed
    const currentStatus = invoiceData.paymentStatus;
    if (currentStatus !== paymentStatus) {
      await event.data?.after.ref.update({
        paymentStatus,
      });
      console.log(`Updated paymentStatus to ${paymentStatus}`);
    } else {
      console.log("No update needed, paymentStatus is already correct.");
    }
  }
);