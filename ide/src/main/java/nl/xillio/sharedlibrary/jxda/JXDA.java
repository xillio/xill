/*
 * Interface to the XDA webservice.
 *
 *
 * Evan Goris, 2014
 */

package nl.xillio.sharedlibrary.jxda;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

public class JXDA {

	private String host = "127.0.0.1";
	private String protocol = "http";
	private int port = 8181;

	public JXDA() {
		this("http", "127.0.0.1", 8181);
	}

	public JXDA(final String protocol, final String host, final int port) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
	}

	public Response predictMetafile(final String modelid, final String metafileid) {
		// Make a prediction.
		//
		// Return:
		// The predicted label.
		return callService("/predict/modelid/" + modelid + "/metafileid/" + metafileid);
	}

	public Response predictModel(final String modelid) {
		// Predict all the documents marked for analysis.
		//
		// Return:
		// The id of the job.
		// Can be used with cancelJob() and statusJob() below.
		return callService("/predict/modelid/" + modelid);
	}

	public Response predict1Model(final String modelid) {
		// Predict all the documents marked for analysis.
		//
		// Return:
		// The id of the job.
		// Can be used with cancelJob() and statusJob() below.
		return callService("/predict1/modelid/" + modelid);
	}

	public Response predictMetafile(final String modelid, final String metafileid, final String datasetid) {
		// Make a prediction and store the prediction in the database.
		//
		// Predictions are stored as part of a dataset, hence the datasetid argument.
		return callService("/predict/modelid/" + modelid + "/metafileid/" + metafileid + "/datasetid/" + datasetid);
	}

	public Response stemMetafile(final String metafileid) {
		return callService("/stem/metafileid/" + metafileid);
	}

	public Response stemDataset(final String datasetid) {
		return callService("/stem/datasetid/" + datasetid);
	}

	public Response indexMetafile(final String metafileid) {
		return callService("/index/metafileid/" + metafileid);
	}

	public Response indexMetafile(final String metafileid, final int stopwordsid) {
		// Index a document taken a set of stop words into account.
		//
		String swid = new Integer(stopwordsid).toString();
		return callService("/index/metafileid/" + metafileid + "/stopwordsid/" + swid);
	}

	public Response indexDataset(final String datasetid) {
		return callService("/index/datasetid/" + datasetid);
	}

	public Response idfDataset(final String datasetid) {
		return callService("/idf/datasetid/" + datasetid);
	}

	public Response trainModel(final String modelid) {
		// Start a training.
		//
		// Return:
		// The id of the job.
		// Can be used with cancelJob() and statusJob() below.
		return callService("/train-svc/modelid/" + modelid);
	}

	public Response trainModel(final String modelid, final float test_ratio) {
		// Train a model where (1-test_ratio) part of the train set will be used for
		// training and test_ratio part of the train set will be used
		// for scoring.
		//
		// Return:
		// The id of the job.
		// Can be used with cancelJob() and statusJob() below.
		//
		// Notes:
		// Don't call this with test_ratio=0.0. Use trainModel(String) instead.
		//
		String ratio = new Float(test_ratio).toString();
		return callService("/train-svc/modelid/" + modelid + "/test_ratio/" + ratio);
	}

	public Response trainModel1(final String modelid) {
		return callService("/train/modelid/" + modelid);
	}

	public Response crossValidate(final String modelid) {
		return callService("/crossvalidate-svc/modelid/" + modelid);
	}

	public Response keywordsMetafile(final String metafileid) {
		return callService("/keywords/metafileid/" + metafileid);
	}

	public Response keywordsDataset(final String datasetid) {
		return callService("/keywords/datasetid/" + datasetid);
	}

	public Response cancelJob(final String jobid) {
		return callService("/cancel/jobid/" + jobid);
	}

	public Response statusJob(final String jobid) {
		return callService("/status/jobid/" + jobid);
	}

	protected Response callService(final String file) {
		Response response = new Response();

		try {
			// Construct URL.
			//
			URL url = new URL(protocol, host, port, file);

			// Open connection
			//
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();

			response.responseCode = con.getResponseCode();
			response.responseMessage = con.getResponseMessage();

			InputStream resp = con.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(resp));

			// Read data from connection
			//
			StringBuffer buff = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				buff.append(line);
			}

			// Return results.
			//
			response.content = new String(buff);
			reader.close();
		} catch (Exception e) {
			String message = "Failed to connect to the XDA service at " + protocol + "://" + host + ":" + new Integer(port).toString();
			System.out.println(message);
			response.content = message;
			response.responseCode = 0;
			response.responseMessage = "";

			e.printStackTrace();
		}
		return response;
	}

	public Response analyse(final URI uri, final Integer modelId) {
		String uri1 = "";
		try {
			uri1 = URLEncoder.encode(uri.toString(), "UTF-8");
			return callService("/analysis/uri/" + uri1 + "/modelid/" + modelId.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Response analyse(final String cacheid, final Integer modelId) {
		return callService("/predictmm/modelid/" + modelId.toString() + "/metafileid/" + cacheid);
	}
}
