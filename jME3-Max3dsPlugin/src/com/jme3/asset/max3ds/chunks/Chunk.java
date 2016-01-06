package com.jme3.asset.max3ds.chunks;

import java.util.HashMap;

import com.jme3.asset.max3ds.M3DLoader;
/**
 * The base class for all chunks. Chunks are  flyweights and should be managed
 * by  {@link ChunkChopper}  Every chunk should know how many bytes of data(or
 * be able figure out)  to read before its subchunks are found. Chunks that only have
 * subchunks need not overrided loadData.
 * Chunks may store data for later use in loadData with {@link pushData}
 * If a chunk needs to initialize components with from subchunks, that data
 * can be retrieved with  {@link ChunkChopper#popData} inside the  {@link
 * #initialize} method.  {@link #loadData} is called at the beginning of the
 * loading process, before any data or subchunks have been loaded(the chunk
 * itself must load its data).
 * <p>
 * During loadData, if the length of data before subchunks is unknown,
 * {@link ChunkChopper#getChunkBytes} may be called and all the data
 * for the chunk(including subchunks) will be read and returned.
 */
public class Chunk 
{
    private HashMap<Integer, Chunk> subChunkMap = new HashMap<Integer, Chunk>();
    private String name;
    private String description;

    /**
     * default no-arg constructror.
     */
    public Chunk(String chunkName)
    {
        name = chunkName;
    }

    /**
     * default no-arg constructror.
     */
    public Chunk()
    {
    }

    public void addSubChunk(Integer id, Chunk subChunk)
    {
        subChunkMap.put(id, subChunk);
    }

    public Chunk getSubChunk(Integer id)
    {
        return (Chunk)subChunkMap.get(id);
    }

    /**
     * This method is called after all the  current chunks subchunks are
     * loaded. Any data stored by those subchunks may be retrieved via {@link
     * ChunkChopper#popData}
     * The default implementation does nothing.
     *
     * @param max3dsLoader may contain data loaded by subchunks. 
     */
    public void initialize(M3DLoader max3dsLoader) 
    {
    }

    /**
     * This is called by the chunk chopper before any of the chunk's 
     * subchunks  are loaded.  Any data loaded that may need to be 
     * used later by superchunks should be stored in
     * the chunk chopper via {@link ChunkChopper#popData}
     * The default implementation does nothing.
     * @param max3dsLoader may contain data loaded by subchunks. 
     */
    public void loadData(M3DLoader max3dsLoader)
    {
    }

    /**
     * Sets  nice human readable name for the chunk.
     * @param name to use for display of this chunk.
     */
    public final void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets a human readable name for the chunk.
     * @return name used to display the chunk.
     */
    public final String getName()
    {
        return name;
    }

    public final String getDescription()
    {
        return description;
    }

    public final void setDescription(String desc)
    {
        description = desc;
    }

    /**
     * Returns the name of this chunk.
     * If the name is null then it just
     * returns the unqualified class name.
     */
    public String toString()
    {
        if (getName() != null)
            return getName();
        String className = getClass().getName();
        return className.substring(className.lastIndexOf('.') + 1, 
            className.length()); 
    }
}
