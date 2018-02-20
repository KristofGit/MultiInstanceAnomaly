package Helper;

import java.util.List;

public class ListHelper {

	public static <T> boolean hasValue(List<T> list)
	{
		return list != null && !list.isEmpty();
	}
}
