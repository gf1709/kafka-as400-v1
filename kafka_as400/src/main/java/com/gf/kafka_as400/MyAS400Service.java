package com.gf.kafka_as400;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.gf.kafka_as400.model.A02;
import com.gf.kafka_as400.model.ZZdPRM;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RequestNotSupportedException;
import com.ibm.as400.access.SystemValue;

@Configuration
public class MyAS400Service {

    private static final Logger log = LoggerFactory.getLogger(MyKafkaConsumer.class);

    static AS400 _as400 = null;
    static String[] _libraryList;
    static String _libUDT;
    static String _libUPC;
    static String _libUTM;
    static String _libMultibanca;
    static String _targaCassaAmbiente;

    AS400 getAS400() {
        boolean isSuccess;
        AS400Message[] messageList;
        if (_as400 == null) {

            _as400 = new AS400(AppConfig.getAS400SystemName(), AppConfig.getAs400User(), AppConfig.getAS400Password());
            CommandCall ccNewCmd = new CommandCall(_as400);
            String strCmd = String.format("CALL PGM(ZZCFGADL) PARM('%-10s')", AppConfig.getAS400Session());
            try {
                Job cmdJob = ccNewCmd.getServerJob();
                isSuccess = ccNewCmd.run(strCmd);
                if (!isSuccess) {
                    log.error("Errore in fase di chiamata al programma che imposta le librerie");
                    _as400 = null;
                    return _as400;
                }
                messageList = ccNewCmd.getMessageList();
                _libraryList = cmdJob.getUserLibraryList();
                for (String lib : _libraryList) {
                    if (lib.trim().endsWith("UDT")) {
                        _libUDT = lib.trim();
                        _targaCassaAmbiente = lib.trim().substring(2, 5);
                    } else if (lib.trim().endsWith("UPC"))
                        _libUPC = lib.trim();
                    else if (lib.trim().endsWith("UTM"))
                        _libUTM = lib.trim();
                    else if (lib.trim().endsWith("UMB"))
                        _libMultibanca = lib.trim();
                }
            } catch (AS400SecurityException | ErrorCompletingRequestException | IOException | InterruptedException
                    | PropertyVetoException | ObjectDoesNotExistException e) {
                e.printStackTrace();
                _as400 = null;
            }
        }
        return _as400;
    }

    static String normalizeSql(String sql) {
        String res = sql.replace(":SCHEMADT", _libUDT).replace(":SCHEMAPC", _libUPC).replace(":SCHEMATM", _libUTM)
                .replace(":SCHEMAMB", _libMultibanca);
        return res;
    }

    public String getCCSID() {

        Object value = "";
        try {
            SystemValue mySysVal = new SystemValue(getAS400(), "QCCSID");
            value = mySysVal.getValue();
        } catch (AS400SecurityException | ErrorCompletingRequestException | InterruptedException | IOException
                | ObjectDoesNotExistException | RequestNotSupportedException e) {
            e.printStackTrace();
        }
        log.info(String.format("Il CCSID AS400 e': %s", value.toString()));
        return value.toString();
    }

    public void callA02Pgm() {
        ProgramParameter[] parmList = new ProgramParameter[2];
        AS400Text prmtext = new AS400Text(36);
        parmList[0] = new ProgramParameter(prmtext.toBytes(" READ"), 36);
        AS400Text dattext = new AS400Text(3840);
        parmList[1] = new ProgramParameter(dattext.toBytes(" "), 3840);
        for (int x = 0; x < 10; x++) {
            ProgramCall pgm = new ProgramCall(getAS400(), QSYSObjectPathName.toPath("LIBFC", "A02A", "PGM"), parmList);

            try {
                if (pgm.run() != true) {
                    // Errore !!
                    AS400Message[] messageList = pgm.getMessageList();
                } else {
                    // OK returned data. Create a converter for this data type

                    Record prmrec = ZZdPRM.from(parmList[0].getOutputData());
                    String cmd = (String) prmrec.getField(ZZdPRM.ZZPCMD);
                    Record a02rec = A02.from(parmList[1].getOutputData());
                    String name = ((String) a02rec.getField(A02.A02COG)).trim();
                    name += ", " + ((String) a02rec.getField(A02.A02NOM)).trim();
                    log.info("name.............: " + name);
                }
            } catch (AS400SecurityException | ErrorCompletingRequestException | IOException | InterruptedException
                    | ObjectDoesNotExistException | PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        String jdbc = "";
        if (AppConfig.getAS400SystemName().toLowerCase().equals("localhost")) {
            String jdbcf = "jdbc:as400://%s;";
            jdbc = String.format(jdbcf, AppConfig.getAS400SystemName());
        } else {
            String jdbcf = "jdbc:as400://%s;user=%s;password=%s;";
            jdbc = String.format(jdbcf, AppConfig.getAS400SystemName(), AppConfig.getAs400User(),
                    AppConfig.getAS400Password());
        }
        return DriverManager.getConnection(jdbc);
    }

    public void readWriteA02() {

        try {
            try {
                Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            } catch (ClassNotFoundException ex) {
                System.err.println("JDBC Driver Not Found.");
            }

            log.info(" ");
            log.info("Lettura via jdbc..............");

            // Apro la connessione
            Connection conn = getConnection();

            String sql = normalizeSql("SELECT A02COG, A02NOM FROM :SCHEMADT.A02 limit 10");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int count = 0;
            while (rs.next()) {
                System.out.println(
                        "Record nr........ " + count + "...:" + rs.getString(1).trim() + ", " + rs.getString(2).trim());
                count++;
                if (count > 10)
                    break;
            }
            sql = normalizeSql(
                    "INSERT INTO :SCHEMADT.A02 (A02SRC,A02CAG,A02COG,A02NOM,A02DEA,A02COA,A02SES,A02DTN) VALUES (?,?,?,?,?,?,?,?)");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "x");
            pstmt.setString(2, "123456");
            pstmt.setString(3, "Zico");
            pstmt.setString(4, "Arthur");
            pstmt.setString(5, "xxx");
            pstmt.setString(6, " ");
            pstmt.setString(7, "x");
            pstmt.setString(8, "01.01.2021");

            log.info("Aggiornamenti via jdbc........");
            int results = pstmt.executeUpdate();
            log.info(String.format("Inserito %d record", results));

            sql = normalizeSql("DELETE FROM :SCHEMADT.A02 WHERE A02SRC=?");
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "x");
            results = pstmt.executeUpdate();
            log.info(String.format("Cancellati %d records", results));

        } catch (SQLException ex) {
            log.error("Errore in fase di connessione o esecuzione al db", ex);
        }
    }

}
