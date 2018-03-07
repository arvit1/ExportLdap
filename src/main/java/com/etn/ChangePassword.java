package com.etn;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedRequest;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedResult;
import com.unboundid.util.LDAPTestUtils;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import javax.net.SocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by nushi on 1/9/2018.
 */
public class ChangePassword {
    static int i = 0;

    public static void main(String[] args) throws LDAPException, IOException {
        TrustManager[] trustManagers = new TrustManager[1];
        trustManagers[0] = new TrustAllTrustManager();

        LDAPConnection conn = null;


        try {
            SSLUtil sslUtil = new SSLUtil(trustManagers);
            SocketFactory factory
                    = sslUtil.createSSLSocketFactory();

            conn = new LDAPConnection(factory, "172.24.1.17", 636);
            conn.bind("cn=Manager,dc=etdirectory,dc=net", "$3v-3\\pIjD");

            changePass("cn=beqir2,ou=General,ou=Accounting,ou=Departments,dc=etdirectory,dc=net", "objectClass=*", conn);

        } catch (LDAPException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static void changePass(String baseDN, String filter, LDAPConnection connection) throws LDAPException{
        PasswordModifyExtendedRequest passwordModifyRequest =
                new PasswordModifyExtendedRequest(
                        baseDN, // The user to update
                        (String) null, // The current password for the user.
                        "kotkot2"); // The new password.  null = server will generate

        PasswordModifyExtendedResult passwordModifyResult;
        try {
            passwordModifyResult = (PasswordModifyExtendedResult)
                    connection.processExtendedOperation(passwordModifyRequest);
            // This doesn't necessarily mean that the operation was successful, since
            // some kinds of extended operations return non-success results under
            // normal conditions.
        } catch (LDAPException le) {
            // For an extended operation, this generally means that a problem was
            // encountered while trying to send the request or read the result.
            passwordModifyResult = new PasswordModifyExtendedResult(
                    new ExtendedResult(le));
        }

        LDAPTestUtils.assertResultCodeEquals(passwordModifyResult,
                ResultCode.SUCCESS);
        String serverGeneratedNewPassword =
                passwordModifyResult.getGeneratedPassword();
        System.out.println(serverGeneratedNewPassword);
    }
}
