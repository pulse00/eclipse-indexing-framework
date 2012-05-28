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
public class QueryBuilder
{
    public static Query createFileQuery(IFile file)
    {
        String filename = file.getName();
        IPath path = file.getFullPath().removeLastSegments(1);
        
        TermQuery query = new TermQuery(new Term(IndexField.PATH, path.toString()));
        TermQuery nameQuery = new TermQuery(new Term(IndexField.FILENAME, filename));
        
        BooleanQuery boolQuery = new BooleanQuery();
        boolQuery.add(query, BooleanClause.Occur.MUST);
        boolQuery.add(nameQuery, BooleanClause.Occur.MUST);
        
        return boolQuery;
    }
    
    public static Query createPathQuery(IPath path)
    {
        IndexingCorePlugin.debug("Getting pathquery for " + path.toString());
        TermQuery query = new TermQuery(new Term(IndexField.PATH, path.toString()));
        return query;
    }

    public static Query createDeleteReferencesQuery(IFile file)
    {
        BooleanQuery boolQuery = new BooleanQuery();
        String path = file.getFullPath().removeLastSegments(1).toString();
        
        boolQuery.add(new TermQuery(new Term(IndexField.PATH, path)), 
                BooleanClause.Occur.MUST
        );
        
        boolQuery.add(new TermQuery( new Term(IndexField.FILENAME, file.getName())),
                BooleanClause.Occur.MUST
        );
        
        boolQuery.add(new TermQuery(new Term(IndexField.TYPE, IndexField.REFERENCE)), 
                BooleanClause.Occur.MUST
        );
        
        return boolQuery;
    }
}
