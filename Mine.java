public class Mine
{
  public static class Node 
  {
    int data;
    Node next;

    public Node()
    {
      this.data = 0;
      this.next = null;
    }
  }

  public static class LinkedListN
  {
    Node head;

    public LinkedListN()
    {
      head = null;
    }
  }

  public static final int CARRY_CALC = 10;

  public static void main (String[] args)
  {
    LinkedListN size3one = new LinkedListN();
    push(size3one, 1);
    push(size3one, 9);
    push(size3one, 5);
    printList(size3one);

    LinkedListN size3two = new LinkedListN();
    push(size3two, 9);
    push(size3two, 7);
    printList(size3two);

/*    LinkedListN size5one = new LinkedListN();
    push(size5one, 1);
    push(size5one, 9);
    push(size5one, 5);
    push(size5one, 0);
    push(size5one, 7);

    LinkedListN size5two = new LinkedListN();
    push(size5two, 6);
    push(size5two, 3);
    push(size5two, 8);
    push(size5two, 7);
    push(size5two, 4);
*/
    LinkedListN finalLL = sumList( size3one, size3two);
    printList(finalLL);

  /*  finalLL = sumList( size5one, size5two);
    printList(finalLL);

    finalLL = sumList( size5one, size3two);
    printList(finalLL);

    finalLL = sumList( size3one, size5two);
    printList(finalLL);*/
  }

  public static LinkedListN sumList( LinkedListN first, LinkedListN second )
  {
    LinkedListN end = new LinkedListN();
    LinkedListN temp = new LinkedListN();

    Node firstN = first.head;
    Node secondN = second.head;

    while(firstN != null || secondN != null )
    {
      if(firstN == null )
      {
        push( temp, secondN.data);
	secondN = secondN.next;
      }	
      else if(secondN == null )
      {
        push( temp, firstN.data);
	firstN = firstN.next;
      }
      else 
      {
        push( temp, firstN.data + secondN.data);
	firstN = firstN.next;
	secondN = secondN.next;
      }
    }

    printList(temp);
    firstN = temp.head; 
    int carry = 0;

    while(firstN != null)
    {
      push(end, (firstN.data + carry) % CARRY_CALC);
      carry = (firstN.data + carry) % CARRY_CALC;
      firstN = firstN.next;
    }

    if( carry != 0 )
    {
      push(end,carry);
    }

    return end;

  }
  
  public static void push ( LinkedListN n, int data)
  {
    if (n == null)
    {
      return;
    }

    Node newHead = new Node();
    newHead.data = data;
    newHead.next = n.head;
    n.head = newHead;

  }

  public static void printList ( LinkedListN n )
  {
    Node iterator = n.head;

    System.out.println("\n------ NEW LIST -------");
    while( iterator != null )
    {
      System.out.println(iterator.data);
      iterator = iterator.next;
    }
    System.out.println("\n------ LIST END -------");
  }

}
