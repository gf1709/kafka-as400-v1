package com.gf.kafka_as400;

import java.beans.PropertyVetoException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
                messageList = ccNewCmd.getMessageList();
            } catch (AS400SecurityException | ErrorCompletingRequestException | IOException | InterruptedException
                    | PropertyVetoException e) {
                e.printStackTrace();
            }
        }
        return _as400;
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
        for (int x = 0; x < 5; x++) {
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

}
