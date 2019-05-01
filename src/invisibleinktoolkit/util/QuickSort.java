/*
 *    Digital Invisible Ink Toolkit
 *    Copyright (C) 2005  K. Hempstalk	
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package invisibleinktoolkit.util;

import java.util.Random;
import java.util.Stack;

/**
 * A quick sorter for 3D arrays.
 * <P>
 * This quick sorter only sorts the first line in multiple dimension arrays. It is
 * assumed that for an array[x][y], the row x = 0 (for all y) is all that will be sorted.
 * The other rows (x >= 1) will remain in place.  For example, an array with 3,2,1 in every
 * row, will be sorted to have 1,2,3 in every row.
 * <P>
 * This sorter is intended to make sorting used in the algorithms a much faster affair.
 *
 * @author Kathryn Hempstalk
 * @deprecated Original method of sorting yields a much faster and efficient operation.
 */
public class QuickSort{
	
	//INNER CLASS
	/**
	 * This class is just for use in my 
	 * quicksort to "simulate" recursion for the 
	 * quick sorting.
	 *
	 */
	private class Position{
		
		/**
		 * Creates a new sorting position with start and end values.
		 * @param s The start position.
		 * @param f The end position.
		 */
		public Position(int s, int f){
			mStart = s;
			mFinish = f;
		}
				
		/**
		 * Gets the start position to sort from.
		 * @return The start position.
		 */
		public int getStart(){
			return mStart;
		}
		
		/**
		 * Gets the end position to sort to.
		 * @return The end position.
		 */
		public int getFinish(){
			return mFinish;
		}
		
		/**
		 * The start position to sort from.
		 */
		private int mStart;
		
		/**
		 * The end position to sort from.
		 */
		private int mFinish;
	}
	
	
	/**
	 * Creates a quicksorter with a given pivot type.
	 *
	 * @param pivot The pivot type for this quicksorter.
	 */
	public QuickSort(int pivot, int useInsertionSort){
		if(pivot != RANDOM_PIVOT && pivot != MEDIAN_PIVOT &&
				pivot != LAST_ELEM_PIVOT)
			mPivotType = RANDOM_PIVOT;
		else
			mPivotType = pivot;
		
		mNumGen = new Random(System.currentTimeMillis());
		mUseInsertion = useInsertionSort;
	}
	
	
	/**
	 * Creates a quick sorter with a random pivot.
	 */
	public QuickSort(){
		mPivotType = RANDOM_PIVOT;
		mNumGen = new Random(System.currentTimeMillis());
		mUseInsertion = 0;
	}
	
	
	/**
	 * Quick sorts an array of integers. 
	 * The property start <= finish should always be true.
	 *
	 * @param anarray The array to be sorted.
	 * @param start The position to start sorting.
	 * @param finish The position to halt sorting.
	 */
	public void rsort(int[][] anarray, int start, int finish){
		//stop our recursion if the size is too small
		if( (finish - start) < 1 || anarray.length < 2){
			return;
		}	
		//just do one move if we only have two elements...
		if( (finish - start) == 1 ){
			if(anarray[0][start] > anarray[0][finish]){
				for(int t = 0; t < anarray.length; t++){
				int temp = anarray[t][start];
				anarray[t][start] = anarray[t][finish];
				anarray[t][finish] = temp;
				}
			}
			return;
		}
		
		//use insertion sort if size of set is too small to 
		//be effective for quicksort
		if( (finish - start) <= mUseInsertion){
			insertionSort(anarray,start,finish);
			return;
		}
		
		//get the pivot position
		int pivpos = this.getPivotPosition(anarray[0], start, finish);
		
		//now comes the fun stuff...
		//first swap e and the last element...
		if(pivpos != finish){
			for(int t = 0; t < anarray.length; t++){
			int temp = anarray[t][pivpos];
			anarray[t][pivpos] = anarray[t][finish];
			anarray[t][finish] = temp;
			}
		}
		
		int pivValue = anarray[0][finish];
		//so now we have an unsorted sequence, and our pivot at the end.
		
		//we need to break into two arrays, one smaller, one larger...
		//let's do this without declaring another array...
		//this means we have smaller numbers at the start of array, 
		//larger at end, pivot in middle (although is initially at the end).
		int s1 = start, s2 = finish - 1;
		while( (s1 - s2) != 0){
			if(anarray[0][s1] > pivValue){
				for(int t = 0; t < anarray.length; t++){
				int temp = anarray[t][s1];
				anarray[t][s1] = anarray[t][s2];
				anarray[t][s2] = temp;
				}
				s2--;
			}
			else
				s1++;
		}
		
		//handle duplicates
		if(anarray[0][s2] <= pivValue)
			s2++;
		
		//swap back the pivot
		for(int t = 0; t < anarray.length; t++){
		int temp = anarray[t][s2];
		anarray[t][s2] = anarray[t][finish];
		anarray[t][finish] = temp;
		}
		
		//now sort the two remaining halves of the array...
		if(s2 == start){
			rsort(anarray,start+1, finish);
		}
		else if(s2 == finish){
			rsort(anarray,start, finish-1);
		}else{
			rsort(anarray, start, s2-1);
			rsort(anarray, s2 + 1, finish);
		}
	}
	
	
	/**
	 * Prints the array to standard out.
	 *
	 * @param sarray The (sorted) array to be printed.
	 */
	public static void printArray(int [][] sarray){
		//print out the array
		for(int t = 0; t < sarray.length; t++){
		System.out.print("Array = { ");
		for(int i = 0; i < sarray[t].length; i++){
			System.out.print(sarray[t][i] + " ");
		}
		System.out.println("}");
		}
	}
	
	
	/**
	 * Gets the position in the array of the pivot point.
	 *
	 * @param anarray The array to be sorted.
	 * @param start The position to start sorting.
	 * @param finish The position to halt sorting.
	 * @return The position of the pivot in the array.
	 */
	private int getPivotPosition(int[] anarray, int start, int finish){
		//get the right pivot type
		if(mPivotType == RANDOM_PIVOT){
			return mNumGen.nextInt( (finish - start) ) + start; //generate a random pivot
		}else if(mPivotType == MEDIAN_PIVOT){ 
			//get the median pivot
			int middle = ((int)(finish - start) / 2) + start;
			int max = QuickSort.getMedian(anarray[start], anarray[middle], anarray[finish]);
			if(max == 0)
				return start;
			else if (max == 1)
				return middle;
			else
				return finish;
			
		}else{ //last element pivot.
			return finish;
		}
	}
	
	/**
	 * Gets the median value of three integers.
	 *
	 * @param a The first value.
	 * @param b The second value.
	 * @param c The third value.
	 * @return The position of the median (0,1,2) = (a,b,c).
	 */
	private static int getMedian(int a, int b, int c){
		//a is middle value
		if((a >= b && a <= c) || (a <= b && a >= c)) return 0;
		//b is middle value
		if((b >= c && b <= a) || (b <= c && b >= a)) return 1;
		//c is middle value
		else
			return 2;
	}
	
	/**
	 * Does an in-place insertion sort on an array of integers.
	 *
	 * @param sarray The array to be sorted.
	 * @param start The start position for sorting.
	 * @param finish The limit position for sorting.
	 */
	private void insertionSort(int[][] sarray, int start, int finish){
		
		//If the array isn't big enough to be unsorted,
		//just return.
		if((finish-start) <= 1) return;
		
		//Starts off with the first element being sorted.
		for(int i = start + 1; i <= finish; i++){
			
			//if it's not in the right place
			if(sarray[0][i] < sarray[0][i-1]){
				
				//find the right place
				int j = start;
				while(sarray[0][i] > sarray[0][j]){
					j++;
				}
				
				for(int t = 0; t < sarray.length; t++){
				//hold our number we want to insert
				int insertable = sarray[t][i];
				
				//now move everything right one
				for(int k = i; k > j; k--){
					sarray[k] = sarray[k-1];
				}
				
				//now insert our number
				sarray[t][j] = insertable;
				}
			}
			//now keep going through the array
		}
	}
	
	
	/**
	 * Iteratively quick sorts an array of integers. 
	 * The property start <= finish should always be true.
	 *
	 * This method solves the problem of "rsort", where for large
	 * arrays the vm recurses too deeply.  It uses its own stack
	 * so info will be put on the heap instead of the recursion stack.
	 *
	 * @param anarray The (3D) array to be sorted.
	 * @param s The position to start sorting.
	 * @param f The position to halt sorting.
	 */
	public void sort(int[][] anarray, int s, int f){
		Stack stack = new Stack();
		try{
			stack.push(new Position(s,f));
			int start, finish;
			do{
				//pop from internal stack
				Position pos = (Position)stack.pop();
				start = pos.getStart();
				finish = pos.getFinish();
				
				if( (finish - start) < 1 || anarray.length < 2){
					//do nothing
				}	
				//just do one move if we only have two elements...
				else if( (finish - start) == 1 ){
					if(anarray[0][start] > anarray[0][finish]){
						for(int t = 0; t < anarray.length; t++){
						int temp = anarray[t][start];
						anarray[t][start] = anarray[t][finish];
						anarray[t][finish] = temp;
					}
					}
				}
				//use insertion sort if size of set is too small to 
				//be effective for quicksort
				else if( (finish - start) + 1 <= mUseInsertion){
					insertionSort(anarray,start,finish);
				}
				else{
					//get the pivot position
					int pivpos = this.getPivotPosition(anarray[0], start, finish);
					
					//now comes the fun stuff...
					//first swap e and the last element...
					if(pivpos != finish){
						for(int t = 0; t < anarray.length; t++){
						int temp = anarray[t][pivpos];
						anarray[t][pivpos] = anarray[t][finish];
						anarray[t][finish] = temp;
						}						
					}
					
					int pivValue = anarray[0][finish];
					
					int s1 = start, s2 = finish - 1;
					while( (s1 - s2) != 0){
						if(anarray[0][s1] > pivValue){
							for(int t = 0; t < anarray.length; t++){
							int temp = anarray[t][s1];
							anarray[t][s1] = anarray[t][s2];
							anarray[t][s2] = temp;
							}
							s2--;
						}
						else
							s1++;
					}
					
					//handle duplicates
					if(anarray[0][s2] <= pivValue)
						s2++;
					
					//swap back the pivot
					for(int t = 0; t < anarray.length; t++){
					int temp = anarray[t][s2];
					anarray[t][s2] = anarray[t][finish];
					anarray[t][finish] = temp;
					}
					
					//push onto the stack the new sorting positions
					stack.push(new Position(start, s2-1));
					stack.push(new Position(s2+1, finish));
				}
				
			}while(!stack.empty());
		}catch(Exception e){
			System.out.println("Pop attempted on empty stack!");
			System.exit(-1);
		}
		
	}
	
	
	//VARIABLES
	
	/**
	 * The number of elements to use insertion sort instead.
	 */
	private int mUseInsertion;
	
	/**
	 * The pivot type of this quick sorter.
	 */
	private int mPivotType;
	
	/**
	 * A random number generator for random pivots
	 */
	private Random mNumGen;
	
	/**
	 * The pivot type for a random pivot.
	 */
	public static final int RANDOM_PIVOT = 0;
	
	/**
	 * The pivot type for a median (first, middle, last) pivot.
	 */
	public static final int MEDIAN_PIVOT = 1;
	
	/**
	 * The pivot type for using the last element as pivot.
	 */
	public static final int LAST_ELEM_PIVOT = 2;
	
}
