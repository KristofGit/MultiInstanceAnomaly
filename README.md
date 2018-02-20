Multi Instance Anomaly Detection
=============

Prototypical implementation for the submission: Kristof Böhmer and Stefanie Rinderle-Ma: Multi Instance Anomaly Detection in Business Process Executions. Presented at BPM 2017.

The prototypical implementation of the presented instance behavior anomaly detection approach was applied during the evaluation of the submission. It analyzes process execution logs, extracts typical behavior, generates a signature that represents that behavior, and applies this signature to identify anomalies in given process execution behavior. Moreover it contains additional algorithms which were only utilized during the evaluation. This are, for example, algorithms to extract historic process executions from various XES logs based on the standard OpenXES parser and a custom SAX based XES parser. The custom parser was created because it provided a better performance than the standard OpenXES parser for the large BPIC XES files.

ReadData
---------

The prototype was split into four projects. First the **ReadData** project which enables to read and prepare XES Logs for the following analysis steps and the evaluation. Hereby, it especially focuses on real life logs, such as, the BPIC 2017, BPIC 2015, and HEP logs. Each of the three log sources uses different approaches, parameter names, and techniques to encode various execution events and aspects, such as, the activity life cycle. Hence, please set the specific log type based on the LogType enumeration. Note, this project also contains the Config class which holds all important settings (e.g., the log type enumeration setting) or the anomaly detection parameters used by this project (e.g., where to search for the yet to be analyzed logfiles, the behavior extraction window size, etc.). Note, during accessing the logs it was ensured that we analyze roughly the same data amount for each log source. Hereby, we can analyze how the complexity of the data (e.g., the dynamics in each trace, which can not be controlled) influences the results "independently" from the overall amount of data which can be controlled.

SignatureGeneration
---------

Secondly, the data extracted by the ReadData project (i.e., XES Logs) is processed by the **SignatureGeneration** project. This project, generates for given logs (i.e., historic typical multi instance behavior) and a given process model a signature. For this, instances that are executed for the given process model are identified, related behavior extraction windows are calculated, and the events which are covered by those windows are extracted. Subsequently, time sequences are mined from these events (one for each window). Finally, all time sequences are merged into a signature that represents typical instance execution behavior (including the typical behavior of all other signatures that are chronologically related to the process model the signature was generate for). Note, that this approach proposes to generate three final granular signatures instead of one large one. Hence, you can choose if you want to generate a signature that represents typical behavior that occurs before, during, or after an instance was executed.

SignatureMatching
---------

Thirdly, the **SignatureMatching** project. This project contains all the necessary code to map given behavior on a signature and to identify if given multi instance executions are anomalous or not. Hereby, it is exploited that the generated signatures enable to calculate the likelihood of mapped behavior. So the given execution events are mined into a time sequence, mapped on the signature, and the behavior likelihood is calculated as the so called execution likelihood. Moreover, a so called reference likelihood is calculated based on executions stored in the historic execution logs which are similar to the given multi instance executions which should be analyzed for anomalies. If the likelihood of the given multi instance executions, to analyze for anomalies, is below the reference likelihood then the analyzed execution is identified as unlikely and, because of this, as anomalous.

Evaluation
---------

Fourthly, the **Evaluation** project. It combines the results of all other projects, for example, to generate signatures or to map behavior onto a signature. Moreover it contains the necessary code for the evaluation. For example, to calculates the metrics (i.e., True Positive, True Negative, False Positive, and False Negative) which are requires to assess the anomaly detection performance of the presented approach. 
