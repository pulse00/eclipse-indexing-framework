/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.index;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import com.dubture.indexing.core.IndexingCorePlugin;

/**
 * 
 * Simple lucene QueryBuilder.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class QueryBuilder {
	public static Query createFileQuery(IFile file, String referenceId) {
		String filename = file.getName();
		IPath path = file.getFullPath();

		IndexingCorePlugin.debug("Getting fileQuery for " + file.getName());

		PrefixQuery query = new PrefixQuery(new Term(IndexField.PATH, path.toString()));
		TermQuery nameQuery = new TermQuery(new Term(IndexField.FILENAME, filename));
		TermQuery refQuery = new TermQuery(new Term(IndexField.TYPE, referenceId));

		BooleanQuery boolQuery = (new BooleanQuery.Builder()).add(query, BooleanClause.Occur.MUST)
				.add(nameQuery, BooleanClause.Occur.MUST).add(refQuery, BooleanClause.Occur.MUST).build();

		IndexingCorePlugin.debug("query: " + boolQuery.toString());
		return boolQuery;
	}

	public static Query createPathQuery(IPath path, String referenceId) {
		IndexingCorePlugin.debug("Getting pathquery for " + path.toString());

		PrefixQuery query = new PrefixQuery(new Term(IndexField.PATH, path.toString()));
		TermQuery refQuery = new TermQuery(new Term(IndexField.TYPE, referenceId));

		BooleanQuery boolQuery = new BooleanQuery.Builder().add(query, BooleanClause.Occur.MUST)
				.add(refQuery, BooleanClause.Occur.MUST).build();

		IndexingCorePlugin.debug("query: " + boolQuery.toString());
		return boolQuery;
	}

	public static Query createDeleteReferencesQuery(IFile file, String type) {
		String path = file.getFullPath().toString();
		BooleanQuery boolQuery = new BooleanQuery.Builder()
				.add(new TermQuery(new Term(IndexField.PATH, path)), BooleanClause.Occur.MUST)
				.add(new TermQuery(new Term(IndexField.FILENAME, file.getName())), BooleanClause.Occur.MUST)
				.add(new TermQuery(new Term(IndexField.TYPE, type)), BooleanClause.Occur.MUST).build();

		IndexingCorePlugin.debug("Creating deleteByReference query: " + boolQuery.toString());

		return boolQuery;
	}
}
