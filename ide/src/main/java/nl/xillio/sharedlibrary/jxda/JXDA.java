/* Interface to the XDA webservice.
 * 
 * 
 * Evan Goris, 2014
 */

package nl.xillio.sharedlibrary.jxda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

public class JXDA {

	private String host     = "127.0.0.1" ;
	private String protocol = "http" ;
	private int    port     = 8181 ;
	
	public JXDA() {
		this("http","127.0.0.1",8181) ;
	}
	
	public JXDA(String protocol,String host,int port) {
		this.protocol = protocol ;
		this.host     = host ;
		this.port     = port ;
	}
	
	public Response predictMetafile(String modelid, String metafileid)  {
		// Make a prediction.
		//
		// Return:
		//	The predicted label.
		return this.callService("/predict/modelid/" + modelid + "/metafileid/" + metafileid) ;
	}
	
	public Response predictModel(String modelid)  {
		// Predict all the documents marked for analysis.
		//
		// Return:
		//	The id of the job.
		//	Can be used with cancelJob() and statusJob() below.
		return this.callService("/predict/modelid/" + modelid) ;
	}

	public Response predict1Model(String modelid)  {
		// Predict all the documents marked for analysis.
		//
		// Return:
		//	The id of the job.
		//	Can be used with cancelJob() and statusJob() below.
		return this.callService("/predict1/modelid/" + modelid) ;
	}

	public Response predictMetafile(String modelid, String metafileid, String datasetid)  {
		// Make a prediction and store the prediction in the database.
		//
		// Predictions are stored as part of a dataset, hence the datasetid argument.
		return this.callService("/predict/modelid/" + modelid + "/metafileid/" + metafileid + "/datasetid/" + datasetid) ;
	}

	public Response stemMetafile(String metafileid)  {
		return this.callService("/stem/metafileid/" + metafileid) ;
	}

	public Response stemDataset(String datasetid)  {
		return this.callService("/stem/datasetid/" + datasetid) ;
	}
	
	public Response indexMetafile(String metafileid)  {
		return this.callService("/index/metafileid/" + metafileid) ;
	}
	
	public Response indexMetafile(String metafileid,int stopwordsid)  {
		// Index a document taken a set of stop words into account.
		//
		String swid = new Integer(stopwordsid).toString() ;
		return this.callService("/index/metafileid/" + metafileid + "/stopwordsid/" + swid) ;
	}

	public Response indexDataset(String datasetid)  {
		return this.callService("/index/datasetid/" + datasetid) ;
	}

	public Response idfDataset(String datasetid)  {
		return this.callService("/idf/datasetid/" + datasetid) ;
	}
	
	public Response trainModel(String modelid)  {
		// Start a training.
		//
		// Return:
		//	The id of the job.
		//	Can be used with cancelJob() and statusJob() below.
		return this.callService("/train-svc/modelid/" + modelid) ;
	}
	
	public Response trainModel(String modelid,float test_ratio)  {
		// Train a model where (1-test_ratio) part of the train set will be used for
		// training and test_ratio part of the train set will be used
		// for scoring.
		//
		// Return:
		//	The id of the job.
		//	Can be used with cancelJob() and statusJob() below.
		//
		// Notes:
		//	Don't call this with test_ratio=0.0. Use trainModel(String) instead.
		//
		String ratio = new Float(test_ratio).toString() ;
		return this.callService("/train-svc/modelid/" + modelid + "/test_ratio/" + ratio) ;
	}

	public Response trainModel1(String modelid) {
		return this.callService("/train/modelid/" + modelid) ;		
	}
	
	public Response crossValidate(String modelid)  {
		return this.callService("/crossvalidate-svc/modelid/" + modelid) ;		
	}
	
	public Response keywordsMetafile(String metafileid)  {
		return this.callService("/keywords/metafileid/" + metafileid) ;
	}
	
	public Response keywordsDataset(String datasetid)  {
		return this.callService("/keywords/datasetid/" + datasetid) ;
	}

	public Response cancelJob(String jobid)  {
		return this.callService("/cancel/jobid/" + jobid) ;		
	}
	
	public Response statusJob(String jobid)  {
		return this.callService("/status/jobid/" + jobid) ;		
	}

	protected Response callService(String file) 
	{		
		Response response        = new Response() ;
		
		try
		{
			// Construct URL.
			//
			URL url = new URL(this.protocol,this.host,this.port,file);
			
			// Open connection
			//
			HttpURLConnection con     = (HttpURLConnection)url.openConnection() ;
			con.setRequestMethod("GET") ;		
			con.connect() ;
			
			response.responseCode    = con.getResponseCode() ;
			response.responseMessage = con.getResponseMessage() ;
			
			InputStream resp      = con.getInputStream() ;
			BufferedReader reader = new BufferedReader(new InputStreamReader(resp)) ;
			
			// Read data from connection
			//
			StringBuffer buff     = new StringBuffer() ;
			String line = "" ;
			while((line=reader.readLine()) != null) {
				buff.append(line) ;
			}
			
			// Return results.
			//
			response.content = new String(buff) ;
			reader.close();
		}
		catch (Exception e)
		{
			String message = "Failed to connect to the XDA service at " + this.protocol + "://" + this.host + ":" + new Integer(this.port).toString() ;
			System.out.println(message);
			response.content         = message ;
			response.responseCode    = 0 ;
			response.responseMessage = "" ;
			
			e.printStackTrace() ;
		}
		return response ;		
	}
	
	public Response analyse(URI uri, Integer modelId)
	{
		String uri1 = "";
		try {
			uri1 = URLEncoder.encode(uri.toString(),"UTF-8");
			return this.callService("/analysis/uri/" + uri1 + "/modelid/" + modelId.toString()) ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	public Response analyse(String cacheid,Integer modelId)
	{
		return this.callService("/predictmm/modelid/" + modelId.toString() + "/metafileid/" + cacheid) ;
	}
}
