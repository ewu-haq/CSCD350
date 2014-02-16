import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import acg.architecture.view.loader.LayoutBundle;
import acg.architecture.view.loader.EntryCircle;
import acg.architecture.view.loader.EntryColor;
import acg.architecture.view.loader.EntryEdge;
import acg.architecture.view.loader.EntryVertex;
import acg.architecture.view.loader.InvalidLayoutException;

public class GlyphLoader {
	
	private String filename;
	private List<EntryCircle> LIST_CIRCLE= new ArrayList<EntryCircle>();
	private List<EntryColor> LIST_COLOR = new ArrayList<EntryColor>();
	private List<EntryEdge> POLYGON = new ArrayList<EntryEdge>();
	private List<List<EntryEdge>> LIST_POLYGON = new ArrayList<List<EntryEdge>>();
	private List<EntryVertex> LIST_VERTEX = new ArrayList<EntryVertex>();
	private int previousVertex= 0 ;
	private int edgeIndex = 1;
		
	  private static final java.lang.String TYPE_COLOR = "c";
	  private static final java.lang.String TYPE_VERTEX = "v";  
	  private static final java.lang.String TYPE_EDGE = "e";  
	  private static final java.lang.String TYPE_CIRCLE = "o";
	  
	  // use same FIELD_TYPE and FIELD_INDEX 
	  private static final int FIELD_TYPE = 0;
	  private static final int FIELD_INDEX = 1;
	  
	  // color 
	  private static final int FIELD_COLOR_RGB = 2;
	  
	  // vertex
	  private static final int FIELD_VERTEX_X = 2;
	  private static final int FIELD_VERTEX_Y = 3;
	  private static final int FIELD_VERTEX_Z = 4;
	  
	  //edge
	  private static final int FIELD_EDGE_VERTEX = 1;
	  private static final int FIELD_EDGE_COLOR = 2;
	  
	  //circle
	  private static final int FIELD_CIRCLE_VERTEX = 1;
	  private static final int FIELD_CIRCLE_COLOR = 2;
	  private static final int FIELD_CIRCLE_RADIUS = 3;
	  
	/*
	 * Constructor of GlyphLoader 
	 * Parameter : filename - the fully qualified layout filename
	 */
	public GlyphLoader(String filename)
	{
		
		this.filename = filename;
	}
	
	/*
	 * 
	 */
	//public LayoutBundle load()
	public LayoutBundle load()
	{
		BufferedReader br = null;
		 
		try {
 
				String sCurrentLine;
				br = new BufferedReader(new FileReader(this.filename));
 
				while ((sCurrentLine = br.readLine()) != null) 
				{
					String[] str_array = sCurrentLine.split(",");
					switch(str_array[FIELD_TYPE])
					{
							case TYPE_COLOR:
							{
								LIST_COLOR.add(new EntryColor(	Integer.parseInt(str_array[FIELD_INDEX]), Color.decode(str_array[FIELD_COLOR_RGB])));
								break;
							}
							
							case TYPE_VERTEX:
							{
								LIST_VERTEX.add(	new EntryVertex(Integer.parseInt(str_array[FIELD_INDEX]), Double.parseDouble(str_array[FIELD_VERTEX_X])
												, Double.parseDouble(str_array[FIELD_VERTEX_Y]), Double.parseDouble(str_array[FIELD_VERTEX_Z]))	);
								break;
							}
							
							case TYPE_EDGE:
							{
									if(previousVertex ==0 ) // previourVertex = 0 means there is first edge of list
									{
										previousVertex = Integer.parseInt(str_array[FIELD_EDGE_VERTEX]);
									}
									else // second elements of edge list counts from here 
									{
										// temp variables 
										// temp values for color and vertex
										EntryVertex temp_prevertex=null;
										EntryVertex temp_curvertex= null;
										EntryColor temp_color= null;
										
										// look for previous Vertex
										for(EntryVertex element: LIST_VERTEX)
										{
											if(element.getIndex() == previousVertex	)
											{
												temp_prevertex = element;
												break;
											}
										}
										// look for current Vertex
										for(EntryVertex element: LIST_VERTEX)
										{
											if(element.getIndex() == Integer.parseInt(str_array[FIELD_EDGE_VERTEX])	)
											{
												temp_curvertex = element;
												break;
											}
										}
										// look for current color
										for(EntryColor element: LIST_COLOR)
										{
											if(element.getIndex() == Integer.parseInt(str_array[FIELD_EDGE_COLOR]))
											{
												temp_color = element;
												break;
											}
										}
										// set up POLYGON
										POLYGON.add(new EntryEdge(edgeIndex, temp_prevertex,temp_curvertex,temp_color ));
										edgeIndex++;
										// set up previous Vertex
										previousVertex = Integer.parseInt(str_array[FIELD_EDGE_VERTEX]);
										
									} // else
								break;
							} // case TYPE_EDGE
							
							case TYPE_CIRCLE :
							{
						
								// temp values for color and vertex
								EntryVertex temp_vertex=null;
								EntryColor temp_color= null;
								
								// search for vertex
								// USE BINARY SEARCH 
								for(EntryVertex element: LIST_VERTEX)
								{
									if(element.getIndex() == Integer.parseInt(str_array[FIELD_CIRCLE_VERTEX])	)
									{
										temp_vertex = element;
										break;
									}
								}
								//search for color
								for(EntryColor element: LIST_COLOR)
								{
									if(element.getIndex() == Integer.parseInt(str_array[FIELD_CIRCLE_COLOR]))
									{
										temp_color = element;
										break;
									}
								}
								
								LIST_CIRCLE.add(	new EntryCircle(Integer.parseInt(str_array[FIELD_INDEX]), 
												temp_vertex, Double.parseDouble(str_array[FIELD_CIRCLE_RADIUS]), temp_color)	);
								break;
							}// case TYPE_CIRCLE
							
							default:
							{
								if(previousVertex !=0) // list poligon is used and needed to be reset
								{
									LIST_POLYGON.add(new ArrayList<EntryEdge>(POLYGON));
									POLYGON.clear();
									edgeIndex = 1;
									previousVertex =0;
								}
							}	
		                 
					}// switch
				} // while ((sCurrentLine = br.readLine()) != null)
				
			} // try 
		catch (IOException e) 
			{
				e.printStackTrace();
			} 
		finally 
			{
				try 
					{
						if (br != null)
								br.close();
					} 
				catch (IOException ex) 
					{
						ex.printStackTrace();
					}
			}
		
		// RETURN LayoutBundle with collections of circles and edges
		return new LayoutBundle( LIST_POLYGON,LIST_CIRCLE );
	}// load();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String temp = "";
		for(EntryColor element: LIST_COLOR)
		{
			temp = temp + element + "\n";
		}
		for(EntryVertex element: LIST_VERTEX)
		{
			temp = temp + element + "\n";
		}
		for(EntryCircle element: LIST_CIRCLE)
		{
			temp = temp + element + "\n";
		}
		
		return temp;
	}
}
