package ch.hsr.solrpayload;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;

public class PayloadSimilarity extends DefaultSimilarity {

	@Override
	public float scorePayload(int doc, int start, int end, BytesRef payload) {
		if (payload == null)
			return 1.0F;
		return PayloadHelper.decodeFloat(payload.bytes, payload.offset);
	}
}
